/*
 * jfreesteel: Serbian eID Viewer GUI Application (GNU AGPLv3)
 * Copyright (C) 2011 Goran Rakic
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License version
 * 3.0 as published by the Free Software Foundation.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public
 * License along with this software; if not, see
 * http://www.gnu.org/licenses/.
 */
package rs.acreno.klijent.licna_karta;

import com.itextpdf.text.DocumentException;
import net.devbase.jfreesteel.EidCard;
import net.devbase.jfreesteel.EidInfo;
import net.devbase.jfreesteel.Reader;
import net.devbase.jfreesteel.Reader.ReaderListener;
import net.devbase.jfreesteel.gui.GUIPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rs.acreno.autoservis.AutoServisController;
import rs.acreno.klijent.Klijent;
import rs.acreno.klijent.ui_klijent.CreateNewKlijentUiController;
import rs.acreno.system.exeption.AcrenoException;
import rs.acreno.system.util.GeneralUiUtility;

import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

/**
 * LicnaKarta is a singleton class behind LicnaKarta application
 *
 * @author Goran Rakic (grakic@devbase.net)
 */
@SuppressWarnings("restriction")  // Access to restricted card APIs
public class LicnaKarta extends JPanel implements ReaderListener {

    private static final long serialVersionUID = -2497143822816312498L;

    private static final String ICON_RESOURCE = "/smart-card-reader2.jpg";

    private final static Logger logger = LoggerFactory.getLogger(EidCard.class);

    private EidInfo info;
    private Image photo;

    private JFrame frame;
    private GUIPanel details;
    private JButton btnPdfExport;
    private JButton btnPopuniPoljaKartica;

    private static LicnaKarta instance;

    public static Klijent klijent = new Klijent();

    public LicnaKarta() throws AcrenoException, SQLException {
        setSize(new Dimension(720, 350));
        setLayout(new CardLayout(0, 0));

        /* Create "insert card" splash screen */
        JPanel splash = new JPanel();
        splash.setBackground(Color.WHITE);
        splash.setLayout(new GridBagLayout());
        ImageIcon insertCardIcon = new ImageIcon(getClass().getResource(ICON_RESOURCE));
        JLabel label = getLabel(insertCardIcon);
        splash.add(label, new GridBagConstraints());

        add(splash, "splash");

        /* Add card details screen */
        details = new EidViewerPanel();
        add(details, "details");
    }

    private JLabel getLabel(ImageIcon insertCardIcon) {
        JLabel label = new JLabel("InsertCard", insertCardIcon, SwingConstants.CENTER);
        Font labelFont = label.getFont();
        label.setFont(
                labelFont.deriveFont(labelFont.getStyle() | Font.BOLD,
                        labelFont.getSize() + 4f));
        return label;
    }

    public static LicnaKarta getInstance() throws AcrenoException, SQLException {
        if (instance == null) {
            instance = new LicnaKarta();
        }
        return instance;
    }

    public void setFrame(JFrame frame) {
        this.frame = frame;
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    createAndShowGUI();
                } catch (AcrenoException | SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Return JVM version
     */
    private static double getVersion() {
        String version = System.getProperty("java.version");
        int pos = 0, count = 0;
        for (; pos < version.length() && count < 2; pos++) {
            if (version.charAt(pos) == '.') count++;
        }
        return Double.parseDouble(version.substring(0, pos - 1));
    }

    static JFrame frame1;

    /**
     * Create the GUI and show it.
     */
    public static void createAndShowGUI() throws AcrenoException, SQLException {
        // Enable font anti aliasing
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        // Set sr_RS locale as default
        Locale.setDefault(new Locale("sr", "RS"));

        // Create and set up the window
        frame1 = new JFrame("bundle.getSt");


        //frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));


        // Set window icon
       /* List<Image> icons = new ArrayList<Image>();
        for (String iconFile : ICON_FILES) {
            try {
                icons.add(ImageIO.read(frame.getClass().getResource(
                    "/" + iconFile)));
            } catch (IOException e) {
                logger.error("Could not find icon file "+iconFile, e);
            }
        }
        frame.setIconImages(icons);*/

        // Set default look and feel
        try {

            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            //Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
            // int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
            //int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
            //frame.setLocationRelativeTo(null);
            int x = (int) (530);
            int y = (int) (250);
            frame1.setLocation(x, y);
            frame1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame1, ("GUIError +  " + e.getMessage()), "GUIErrorTitle",
                    JOptionPane.WARNING_MESSAGE);
            logger.error("Error setting look and feel", e);
        }

        // Test for Java 1.6 or newer
        if (getVersion() < 1.6) {
            JOptionPane.showMessageDialog(frame1, "JavaError", "JavaErrorTitle",
                    JOptionPane.ERROR_MESSAGE);
           // System.exit(1);
        }

        // Get the list of terminals
        CardTerminal terminal = null;
        try {
            TerminalFactory factory = TerminalFactory.getDefault();
            terminal = pickTerminalGUI(frame1, factory.terminals().list());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame1,
                    "ReaderError" + ": " + e.getMessage(),
                    "ReaderErrorTitle",
                    JOptionPane.ERROR_MESSAGE);
            logger.error("Reader error", e);
            //System.exit(1);
        }

        // Create and set up the content pane
        LicnaKarta app = LicnaKarta.getInstance();
        app.setFrame(frame1);
        frame1.getContentPane().add(app, BorderLayout.CENTER);
        frame1.pack();

        // Create reader and add GUI as the listener
        Reader reader = new Reader(terminal);
        reader.addCardListener(app);

        // Display the window
        frame1.setVisible(true);
    }

    public static CardTerminal pickTerminalGUI(JFrame frame, List<CardTerminal> terminals) {
        if (terminals.size() == 1) {
            return terminals.get(0);
        }

        CardTerminal terminal = (CardTerminal) JOptionPane.showInputDialog(
                frame,
                "SelectReader",
                "SelectReaderTitle",
                JOptionPane.PLAIN_MESSAGE,
                null,
                terminals.toArray(),
                terminals.get(0));

        // Cancel clicked
        if (terminal == null){
            //System.exit(1);
        }

        return terminal;
    }

    private void showCardError(Exception e) {
        JOptionPane.showMessageDialog(this,
                "CardError" + ": " + e.getMessage(),
                "CardErrorTitle",
                JOptionPane.ERROR_MESSAGE);
        logger.error("Card error", e);
    }

    public void inserted(final EidCard card) {
        logger.info("Card inserted");
        CardLayout cl = (CardLayout) this.getLayout();
        cl.show(this, "details");

        try {
            info = card.readEidInfo();

            String ime = info.getGivenName();
            String prezime = info.getSurname();
            String imePrezimeToLatin = GeneralUiUtility.transliterate(ime + " " + prezime);
            String JMBG = info.getPersonalNumber();
            String brLicneKarte = info.getDocRegNo();
            String ulicaBroj = info.getStreet() + " " + info.getHouseNumber();
            String mesto = info.getPlace();

            klijent.setImePrezime(imePrezimeToLatin);
            klijent.setBrLicneKarte(brLicneKarte);
            klijent.setUlicaBroj(ulicaBroj);
            klijent.setMesto(mesto);
            klijent.setMaticniBroj(JMBG);

            System.out.println(ime);
            System.out.println(JMBG);
            System.out.println(brLicneKarte);

            details.setDetails(info);
            photo = card.readEidPhoto();
            details.setPhoto(photo);
            btnPdfExport.setEnabled(true);
            btnPopuniPoljaKartica.setEnabled(true);

        } catch (Exception e) {
            showCardError(e);
        }
    }

    public void removed() {
        logger.info("Card removed");

        CardLayout cl = (CardLayout) this.getLayout();
        cl.show(this, "splash");

        btnPdfExport.setEnabled(false);
        btnPopuniPoljaKartica.setEnabled(false);
        info = null;
        photo = null;
        details.clearDetailsAndPhoto();
    }

    /**
     * UI panel for the application
     */
    class EidViewerPanel extends GUIPanel {

        private static final long serialVersionUID = 1L;

        public EidViewerPanel() {
            super();
            btnPdfExport = btnPdfExport("Sačuvaj PDF");
            btnPopuniPoljaKartica = btnPopuniPoljaKartica("Popuni polja u kartici");
            toolbar.add(btnPdfExport, BorderLayout.WEST);
            toolbar.add(btnPopuniPoljaKartica, BorderLayout.EAST);
        }

        private JButton btnPdfExport(String text) {
            JButton button = new JButton(text);
            button.setEnabled(false);
            button.setPreferredSize(new Dimension(130, 36));
            button.setSize(new Dimension(200, 0));
            button.addActionListener(new ButtonActionListenerPDFexport());
            return button;
        }

        private JButton btnPopuniPoljaKartica(String text) {
            JButton button = new JButton(text);
            button.setEnabled(false);
            button.setPreferredSize(new Dimension(130, 36));
            button.setSize(new Dimension(200, 0));
            button.addActionListener(new ButtonActionListenerPopuniKarticu());
            return button;
        }
    }

    private class ButtonActionListenerPopuniKarticu implements ActionListener {
        @Override public void actionPerformed(ActionEvent e) {
            System.out.println("STISNUTO DUGME POPUNI KARTICU");
            try {
                AutoServisController.ucitajLicnuKartu(klijent);
                CreateNewKlijentUiController.ucitajLicnuKartu(klijent);
                JComponent comp = (JComponent) e.getSource();
                Window win = SwingUtilities.getWindowAncestor(comp);
                win.dispose();
            } catch (Exception acrenoException) {
                acrenoException.printStackTrace();
            }
        }
    }

    /**
     * The action taken on the UI action button press.
     */
    private class ButtonActionListenerPDFexport implements ActionListener {
        @Override public void actionPerformed(ActionEvent ev) {

            final JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(new File("report_" + info.getPersonalNumber() + ".pdf"));
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "PDF", "pdf");
            fc.setFileFilter(filter);
            int returnVal = fc.showSaveDialog(frame);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                // Append correct extension if missing
                String filename = fc.getSelectedFile().toString();
                if (!filename.toLowerCase().endsWith(".pdf")) {
                    filename += ".pdf";
                }

                try {
                    logger.info("Saving " + filename);
                    PdfReport report = new PdfReport(info, photo);
                    report.write(filename);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(frame,
                            "SavePDFError" + ": " + e.getMessage(),
                            "SavePDFErrorTitle",
                            JOptionPane.ERROR_MESSAGE);
                    logger.error("Error saving PDF file", e);
                } catch (DocumentException e) {
                    JOptionPane.showMessageDialog(frame,
                            "CreatePDFError" + ": " + e.getMessage(),
                            "CreatePDFErrorTitle",
                            JOptionPane.ERROR_MESSAGE);
                    logger.error("Error creating PDF file", e);
                }
            }
        }
    }
}
