# "AC Reno" Auto Servis Application
![build](https://api.travis-ci.org/acrenoai/ACRenoApp.svg?branch=master&status=passed)

Open source "AC Reno" aplikacija za vođenje tekućih poslova oko evidencije 
popravki i održavanja automobila.

###### Currrent release is: BETA 1.0

#### System Requirements

---
- **JAVA JDK 14**
- **JAVAFX 14**
- **MS ACCESS** 2019 (because is DB currently implemented in MS ACCESS)

#### Korisne komande

---
Add VM options for run JavaFX app on compile runtime:

```shell
--module-path /PATH/TO/YOUR/JAVA/FX/javafx-sdk-14/lib 
                                        --add-modules javafx.controls,javafx.fxml
```

Where is '/PATH/.../.../FX/javafx-sdk-14/lib'  your JavaFx lib directory.

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

More information on about the [LICENSE can be found here](https://github.com/acrenoai/ACRenoApp/blob/dev-1.0/LICENSE.md)
