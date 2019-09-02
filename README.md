# KettleLog

Kettlelog is an inventory manager designed to help business owners pinpoint exactly when an item should be reordered to avoid inventory shortages. It's easy-to-use and is always up to date, giving you the most accurate information possible. It is written completely in Java & JavaFX, and saves all user information in SQLite databases.

### Features

* Simple & Attractive UI 

![](https://github.com/rhyzue/KettleLog/blob/master/Screenshots/maintable.png) 

* Multifunctional Editing

| ![](https://github.com/rhyzue/KettleLog/blob/master/Screenshots/edit.png) | 
|:--:| 
| *Edit or log an item easily through the same popup.* |

* Edit/Delete Logs

| ![](https://github.com/rhyzue/KettleLog/blob/master/Screenshots/logtable.png) | 
|:--:| 
| *Easily edit your past logs using our log table.* |

* Statistics & Graph 

Item Statistics            |  Consumption Graph
:-------------------------:|:-------------------------:
![](https://github.com/rhyzue/KettleLog/blob/master/Screenshots/stats.png)  |  ![](https://github.com/rhyzue/KettleLog/blob/master/Screenshots/graph.png)

* Notifications

| <img src="https://github.com/rhyzue/KettleLog/blob/master/Screenshots/notifs.png"/> | 
|:--:| 
| *Get notified when an item needs re-ordering. Kettlelog will calculate the re-order date for you!* |

### Installation Instructions

The KettleLog team is currently still trying to find an efficient way to deploy our application as an .exe and .dmg file. 

For the time being, KettleLog can be run by completing the following steps: 

1. Click on the green "Clone or Download" button, and select "Download ZIP."

**insert image here**


**Windows**
1. Run the following three commands in order to open KettleLog. 
We are considering writing a quick shell script to help make this process easier!

```
del *.class
javac --module-path /path-to-project/Kettlelog/jfxwin/lib --add-modules javafx.controls Kettlelog.java
java --module-path /path-to-project/Kettlelog/jfxwin/lib --add-modules javafx.controls Kettlelog
```
**MacOS**

1. Navigate to the project's 'bin' folder by using the following command: 

```
cd path-to-project/Kettlelog/bin
```
2. Run our bash script and KettleLog should open immediately!

```
sh boil.sh
```
