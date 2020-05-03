package rs.acreno.system.DAO;

public class SqlQuerys {

    //SQLQ KLIJENTI

    public static final String INSERT_KLIJENT_IN_TABLE =
            "INSERT INTO Klijenti (" +
                    " IdKlijenta, imePrezime, Mesto, PostanskiBroj, UlicaBroj, BrLicneKarte, maticniBroj," +
                    " ostaliDetalji, email, telefonMobilni, telefonFiksni, web, brojRacuna, banka )" +
                    "VALUES (null,?,?,?,?,?,?,?,?,?,?,?,?,?);";

    public static final String UPDATE_KLIJENT_TABLE =
            "UPDATE Klijenti SET " +
                    "imePrezime=?," +
                    " mesto=?," +
                    " postanskiBroj=?," +
                    " ulicaBroj=?," +
                    " brLicneKarte=?," +
                    " maticniBroj=?," +
                    " ostaliDetalji=? ," +
                    " email=? ," +
                    " telefonMobilni=? ," +
                    " telefonFiksni=? ," +
                    " web=? ," +
                    " brojRacuna=? ," +
                    " banka=? " +
                    "WHERE IdKlijenta=?";

    public static final String FIND_ALL_KLIJENTS = "SELECT * FROM Klijenti";

    //SQLQ AUTOMOBILI
    public static final String INSERT_AUTOMOBIL_IN_TABLE =
            "INSERT INTO Automobil ( IdAuta, Idklijenta, regOznaka, kilomteraza, vrstaVozila, markaVozila, modelVozila," +
                    " godisteVozila, zapreminaVozila, snagaVozila, vinVozila, brojMotoraVozila, vrstaGorivaVozila," +
                    " bojaVozila, masaVozila, najvecaDozvoljenaMasaVozila, datumPrveRegistracijeVozila," +
                    " brojMestaZaSedenje, brojVrataVozila, napomeneAutomobila )" +
                    "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    public static final String FIND_ALL_AUTOMOBILI = "SELECT * FROM Automobil";

    public static final String UPDATE_CAR_TABLE =
            "UPDATE Automobil SET " +
                    "idKlijenta=?," +
                    "regOznaka=?," +
                    "kilomteraza=?," +
                    "vrstaVozila=?," +
                    "markaVozila=?," +
                    "modelVozila=?," +
                    "godisteVozila=?," +
                    "zapreminaVozila=?," +
                    "snagaVozila=?," +
                    "vinVozila=?," +
                    "brojMotoraVozila=?," +
                    "vrstaGorivaVozila=?," +
                    "bojaVozila=?," +
                    "masaVozila=?, " +
                    "najvecaDozvoljenaMasaVozila=?, " +
                    "datumPrveRegistracijeVozila=?, " +
                    "brojMestaZaSedenje=?," +
                    "brojVrataVozila=?," +
                    "napomeneAutomobila=? WHERE idAuta=?";

    //SQLQ DEFEKTAZA
    public static final String CREATE_TABLE_DEFEKTAZA =
            "CREATE TABLE IF NOT EXISTS \"Defektaza\" (\n" +
                    "\t\"idDefektaze\"\tINTEGER PRIMARY KEY AUTOINCREMENT UNIQUE,\n" +
                    "\t\"idAuta\"\tINTEGER NOT NULL,\n" +
                    "\t\"opisDefektaze\"\tTEXT,\n" +
                    "\t\"datumDefektaze\"\tTEXT,\n" +
                    "\t\"ostaliDetaljiDefektaze\"\tTEXT,\n" +
                    "FOREIGN KEY (idAuta)\n" +
                    "       REFERENCES Automobil (idAuta) \n" +
                    ");";
    public static final String INSERT_INTO_DEFEKTAZA_TABLE =
            "INSERT INTO Defektaza(" +
                    "idDefektaze" +
                    ", idAuta" +
                    ", opisDefektaze" +
                    ", datumDefektaze" +
                    ", ostaliDetaljiDefektaze)\n" +
                    "VALUES(null, ?, ?, ?, ?);";

    public static final String FIND_ALL_DEFEKTAZE = "SELECT * FROM Defektaza";
    public static final String FIND_ALL_DEFEKTAZE_BY_PROPERTY = "SELECT * FROM Defektaza WHERE ";
    public static final String DELETE_FROM_TABLE_DEFEKTAZA = "DELETE FROM Defektaza WHERE idDefektaze=?";
    public static final String UPDATE_DEFEKTAZA_TABLE = "" +
            "UPDATE Defektaza\n" +
            "SET idAuta = ?, opisDefektaze = ?, datumDefektaze = ?, ostaliDetaljiDefektaze = ?\n" +
            "WHERE idDefektaze=?";

    //SQLq USLUGE
    public static final String CREATE_TABLE_USLUGE =
            "CREATE TABLE IF NOT EXISTS \"Usluge\" (\n" +
                    "\t\"idUsluge\"\tINTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,\n" +
                    "\t\"nazivUsluge\"\tTEXT,\n" +
                    "\t\"opisUsluge\"\tTEXT,\n" +
                    "\t\"cenaUsluge\"\tREAL,\n" +
                    "\t\"kolicina\"\tINTEGER,\n" +
                    "\t\"popust\"\tINTEGER\n" +
                    ");";

    public static final String UPDATE_USLUGE_TABLE = "" +
            "UPDATE Usluge\n" +
            "SET nazivUsluge = ?, opisUsluge = ?, cenaUsluge = ?, kolicina = ?\n, popust = ?\n" +
            "WHERE idUsluge=?";

    public static final String FIND_ALL_USLUGE = "SELECT * FROM Usluge";

    public static final String INSERT_INTO_USLUGE =
            "INSERT INTO Usluge(idUsluge, nazivUsluge, opisUsluge, cenaUsluge, kolicina, popust)\n" +
                    "VALUES(null, ?, ?, ?, ?, ?);";
    public static final String FIND_ALL_USLUGE_BY_PROPERTY = "SELECT * FROM Usluge WHERE ";
    public static final String DELETE_FROM_TABLE_USLUGE = "DELETE FROM Usluge WHERE idUsluge=?";

    //SQLq POSAO USLUGE DAO
    public static final String CREATE_TABLE_POSAO_USLUGE_DAO =
            "CREATE TABLE  IF NOT EXISTS \"PosaoUsluge\" (\n" +
                    "\t\"idPosaoUsluge\"\tINTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,\n" +
                    "\t\"idRacuna\"\tINTEGER NOT NULL,\n" +
                    "\t\"idUsluge\"\tINTEGER NOT NULL,\n" +
                    "\t\"cena\"\tREAL,\n" +
                    "\t\"nabavnaCena\"\tREAL,\n" +
                    "\t\"kolicina\"\tINTEGER,\n" +
                    "\t\"jedinicaMere\"\tTEXT,\n" +
                    "\t\"popust\"\tINTEGER,\n" +
                    "\t\"opisPosaoArtiklli\"\tTEXT,\n" +
                    "\t\"detaljiPosaoArtikli\"\tINTEGER,\n" +
                    /*"\tPRIMARY KEY(\"idRacuna\",\"idArtikla\")\n" +*/
                    "FOREIGN KEY (idRacuna)\n" +
                    "       REFERENCES Racuni (idRacuna) \n" +
                    "FOREIGN KEY (idUsluge)\n" +
                    "       REFERENCES Usluge (idUsluge) \n" +
                    ");";

    public static final String INSERT_INTO_POSAO_USLUGE_DAO =
            "INSERT INTO PosaoUsluge(" +
                    "  idPosaoUsluge" +
                    ", idRacuna" +
                    ", idUsluge" +
                    ", cena" +
                    ", nabavnaCena" +
                    ", kolicina" +
                    ", jedinicaMere" +
                    ", popust" +
                    ", opisPosaoArtiklli" +
                    ", detaljiPosaoArtikli)\n" +
                    "VALUES(null, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

    public static final String UPDATE_POSAO_USLUGE_DAO_TABLE = "" +
            "UPDATE PosaoUsluge\n" +
            "SET " +
            "  idRacuna = ?" +
            ", idUsluge = ?" +
            ", cena = ?" +
            ", nabavnaCena = ?" +
            ", kolicina = ?" +
            ", jedinicaMere = ?" +
            ", popust = ?" +
            ", opisPosaoArtiklli = ?" +
            ", detaljiPosaoArtikli = ?" +
            " WHERE idPosaoUsluge=?";

    public static final String DELETE_FROM_TABLE_USLUGE_DAO = "DELETE FROM PosaoUsluge WHERE idRacuna=? AND idUsluge=?";

    public static final String FIND_ALL_POSAO_USLUGE_DAO_BY_PROPERTY = "SELECT * FROM PosaoUsluge WHERE ";

    public static final String FIND_ALL_POSAO_USLUGE_DAO = "SELECT * FROM PosaoUsluge";

    //SQLq ARTIKLI

    public static final String INSERT_INTO_ARTIKLE =
            "INSERT INTO Artikli ( idArtikla, kataloskiBrArtikla, nazivArtikla, opisArtikla, jedinicaMere," +
                    " kolicina, nabavnaCenaArtikla, cenaArtikla )" +
                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?);";

    public static final String UPDATE_ARTIKLI_TABLE = "" +
            "UPDATE Artikli\n" +
            "SET " +
            "kataloskiBrArtikla = ?" +
            ", nazivArtikla = ?" +
            ", opisArtikla = ?" +
            ", jedinicaMere = ?" +
            ", kolicina = ?" +
            ", nabavnaCenaArtikla = ?" +
            ", cenaArtikla = ?" +
            "WHERE idArtikla=?";

    public static final String DELETE_FROM_TABLE_ARTIKLI = "DELETE FROM Artikli WHERE idArtikla=?";

    public static final String FIND_ALL_ARTIKLE_BY_PROPERTY = "SELECT * FROM Artikli WHERE ";

    public static final String FIND_ALL_ARTIKLE = "SELECT * FROM Artikli";

    //SQLq POSAO ARTIKLI DAO
    public static final String INSERT_INTO_POSAO_ARTIKLI_DAO =
            "INSERT INTO PosaoArtikli(" +
                   /* "  idPosaoArtikli" +*/
                    " idRacuna" +
                    ", idArtikla" +
                    ", cena" +
                    ", nabavnaCena" +
                    ", kolicina" +
                    ", jedinicaMere" +
                    ", popust" +
                    ", opisPosaoArtiklli" +
                    ", detaljiPosaoArtikli)\n" +
                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?);";

    public static final String UPDATE_POSAO_ARTIKLI_DAO_TABLE = "" +
            "UPDATE PosaoArtikli\n" +
            "SET " +
            "  idRacuna = ?" +
            ", idArtikla = ?" +
            ", cena = ?" +
            ", nabavnaCena = ?" +
            ", kolicina = ?" +
            ", jedinicaMere = ?" +
            ", popust = ?" +
            ", opisPosaoArtiklli = ?" +
            ", detaljiPosaoArtikli = ?" +
            " WHERE idPosaoArtikli=?";

    public static final String DELETE_FROM_TABLE_POSAO_ARTIKLI_DAO = "DELETE FROM PosaoArtikli WHERE idRacuna=? AND idArtikla=?";

    public static final String FIND_ALL_POSAO_ARTIKLE_DAO_BY_PROPERTY = "SELECT * FROM PosaoArtikli WHERE ";

    public static final String FIND_ALL_POSAO_ARTIKLE_DAO = "SELECT * FROM PosaoArtikli";

    //SQLq RADNI NALOZI
    public static final String CREATE_RADNI_NALOG_TABLE_QUERY = "" +
            "CREATE TABLE \"RadniNalog\" (\n" +
            "\t\"IdRadnogNaloga\"\tINTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,\n" +
            "\t\"IdAutomobila\"\tINTEGER NOT NULL,\n" +
            "\t\"Datum\"\tTEXT,\n" +
            "\t\"DetaljiStranke\"\tTEXT,\n" +
            "\t\"DetaljiServisera\"\tTEXT,\n" +
            "\t\"DaLiJePonudaIliRacun\"\tNUMERIC,\n" +
            "\t\"Kilometraza\"\tTEXT,\n" +
            "FOREIGN KEY (IdAutomobila)\n" +
            "       REFERENCES Automobil (idAuta) \n" +
            ");";

    public static final String INSERT_RADNI_NALOG_IN_TABLE =
            "INSERT INTO RadniNalog(" +
                    "  IdRadnogNaloga" +
                    ", IdAutomobila" +
                    ", Datum" +
                    ", DetaljiStranke" +
                    ", DetaljiServisera" +
                    ", DaLiJePonudaIliRacun" +
                    ", Kilometraza)\n" +
                    "VALUES(null, ?, ?, ?, ?, ?, ?);";
    ;

    public static final String UPDATE_RADNI_NALOG_TABLE =
            "UPDATE RadniNalog\n" +
                    "SET " +
                    "IdAutomobila = ?" +
                    ", Datum = ?" +
                    ", DetaljiStranke = ?" +
                    ", DetaljiServisera = ?" +
                    ", DaLiJePonudaIliRacun = ?" +
                    ", Kilometraza = ?" +
                    "WHERE IdRadnogNaloga=?";

    public static final String FIND_ALL_RADNI_NALOG = "SELECT * FROM RadniNalog";

    //SQLQ RACUNI

    public static final String INSERT_RACUN_IN_TABLE =
            "INSERT INTO Racuni(" +
                    "  idRacuna" +
                    ", IdAutomobila" +
                    ", datum" +
                    ", popust" +
                    ", napomeneRacuna)\n" +
                    "VALUES(?, ?, ?, ?, ?);";

    public static final String UPDATE_RACUN_TABLE = "" +
            "UPDATE Racuni\n" +
            "SET " +
            "IdAutomobila = ?" +
            ", datum = ?" +
            ", popust = ?" +
            ", napomeneRacuna = ?" +
            "WHERE idRacuna=?";

    public static final String DELETE_FROM_RACUN = "DELETE FROM Racuni WHERE idRacuna=?";

    public static final String FIND_ALL_RACUNE_BY_PROPERTY = "SELECT * FROM Racuni WHERE ";

    public static final String FIND_ALL_RACUNE = "SELECT * FROM Racuni";

}