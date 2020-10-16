# [_"AC Reno" Auto Servis_ Informacioni Sistem](https://acreno.rs)
![build](https://img.shields.io/travis/acrenoai/ACRenoApp/master?logo=GitHub&style=flat-square)
![build](http://img.shields.io/badge/version-BETA%201.3-brightgreen?style=flat-square&logo=GitHub)
![build](http://img.shields.io/badge/licence-GPL%203-brightgreen?style=flat-square&logo=GNU)
![build](http://img.shields.io/badge/code-JavaFX-blue?style=flat-square&logo=Java)

###### Currrent release is: BETA 1.3

<p align="left">
  <img width="300" height="170" src="https://www.acreno.rs/wp-content/uploads/media/acr-slpash.png">
  <img width="300" height="170" src="https://www.acreno.rs/wp-content/uploads/media/acr-home-screen.png">
</p>


Open source [**"_AC Reno_"**](https://acreno.rs) aplikacija za vođenje tekućih poslova oko evidencije 
popravki i održavanja automobila. 

* Aplikaciju razvija i održava dev tim [**"_AC Reno_"**](https://acreno.rs) Auto Servisa iz Novog Sada.

* Oficijalna web stranica servisa: [**www.acreno.rs**](https://acreno.rs) 

#### System Requirements:

---
- **JAVA JDK 14**
- **JAVAFX 14**
- **MS ACCESS** 2019 (because is DB currently implemented in MS ACCESS)

#### Korisne komande:

---
Add VM options for run JavaFX app on compile runtime:

```shell
--module-path /PATH/TO/YOUR/JAVA/FX/javafx-sdk-14/lib 
                                        --add-modules javafx.controls,javafx.fxml
```

Where is '/PATH/.../.../.../FX/javafx-sdk-14/lib'  your JavaFx lib directory.

---
Run App command
```shell
java -jar --module-path ${JAVA_FX} 
        --add-modules javafx.controls,javafx.fxml,javafx.web,javafx.graphics,javafx.media ACReno.jar
```

 GPL3 LICENSE SYNOPSIS
---

**_TL;DR_*** Here's what the license entails:

```markdown
1. Anyone can copy, modify and distribute this software.
2. You have to include the license and copyright notice with each and every distribution.
3. You can use this software privately.
4. You can use this software for commercial purposes.
5. If you dare build your business solely from this code, you risk open-sourcing the whole code base.
6. If you modify it, you have to indicate changes made to the code.
7. Any modifications of this code base MUST be distributed with the same license, GPLv3.
8. This software is provided without warranty.
9. The software author or license can not be held liable for any damages inflicted by the software.
```

More information on about the [LICENSE can be found here](https://github.com/acrenoai/ACRenoApp/blob/beta-1-0/LICENSE.md)
