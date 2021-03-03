# EasyPhotos    
[![](https://jitpack.io/v/HuanTanSheng/EasyPhotos.svg)](https://jitpack.io/#HuanTanSheng/EasyPhotos)    

QQ交流群：[288600953](https://jq.qq.com/?_wv=1027&k=5QGgCDe)    
[demo下载](https://raw.githubusercontent.com/HuanTanSheng/EasyPhotos/master/demo/release/demo-release.apk)    
[demo无法下载解决方案](https://blog.csdn.net/Mirt_/article/details/106011435)
  


| 无选中状态（默认UI色调）   | 选中状态（ [自定义UI色调](https://github.com/HuanTanSheng/EasyPhotos/wiki/10-%E8%87%AA%E5%AE%9A%E4%B9%89UI%E6%A0%B7%E5%BC%8F)）  | 其他功能（各功能可自选） |
|:-----------:|:--------:|:---------:|
|![](https://i.loli.net/2021/03/03/5WH96lA4L8nKxo2.png) | ![](https://i.loli.net/2021/03/03/svxeoD6ycPu1OqN.png) | ![](https://i.loli.net/2021/03/03/Pw1ZJ5l7YgmzctD.png)|

| 专辑列表（默认UI色调） | 相册带广告（ [自定义UI色调](https://github.com/HuanTanSheng/EasyPhotos/wiki/10-%E8%87%AA%E5%AE%9A%E4%B9%89UI%E6%A0%B7%E5%BC%8F)） | 专辑列表带广告 |
|:-------:|:---------:|:---------:|
|![](https://i.loli.net/2021/03/03/wIQWPyV7XBs9hRo.png) | ![](https://i.loli.net/2021/03/03/bUY2CoMAG6ljZ8v.png) | ![](https://i.loli.net/2021/03/03/NvrCz3EJTOFLtwn.png)|  

| 预览页   |  预览页单击图片转全屏模式 | 预览页缩放图片 | 
|:-------:|:---------:|:---------:|
|![](https://i.loli.net/2021/03/03/dFoPMGl9KUne5Oa.png) | ![](https://i.loli.net/2021/03/03/VftUlwEO61PuGeY.png) | ![](https://i.loli.net/2021/03/03/E9zBil6WFAq4Xwc.png)|

|预览页单击缩放图片显示操作栏 | 拼图选择页| 拼图选择页 |
|:-------:|:---------:|:---------:|
|![](https://i.loli.net/2021/03/03/as87EV531gCevLP.png) | ![](https://i.loli.net/2021/03/03/jo8T3CcPZbGkDUi.png) | ![](https://i.loli.net/2021/03/03/4YnsqWKDZEIFoBa.png)|    

|拼图页 | 拼图页拼图功能| 拼图页文字贴纸功能 |
|:-------:|:---------:|:---------:|
|![](https://i.loli.net/2021/03/03/U65yWR4FBkbDPC9.png) | ![](https://i.loli.net/2021/03/03/OrZEWdwFh4K1Pyk.png) | ![](https://i.loli.net/2021/03/03/cnZyOAbF176vlzD.png)|

|文字贴纸编辑页 | 示例功能列表 | 示例功能列表 |
|:-------:|:---------:|:---------:|
|![](https://i.loli.net/2021/03/03/4zBbLmpnxiHd5Jy.png) | ![](https://i.loli.net/2021/03/03/PUk2rTl5CRsxNJb.png) | ![](https://i.loli.net/2021/03/03/123Jtx7AlmDc9Lw.png)|    
    
    
## 产品特色    
- 兼容android 10
- 支持绑定Glide、Picasso、Imageloader等所有图片加载库（fresco暂不支持），EasyPhotos并没有对他们进行依赖，不必担心冲突和体积问题。     
- 状态栏字体颜色智能适配，当状态栏颜色趋近于白色时，字体颜色智能处理为深色     
- 内部处理运行时权限，使用者无需考虑权限问题    
- 清晰预览超大图和长图  
- 拼一张功能（可配置开关，可独立作为拼图使用）    
- 原图功能（可配置开关）    
- 广告填充（可配置开关）     
- 过滤图片（图片宽度、图片高度、文件大小三个维度任意选择和搭配）
- 默认勾选图片（可配置）    
- 图片预览（可全屏，可缩放）    
- 支持动图gif显示，并支持只显示动图gif
- 支持视频video显示，并支持只显示视频video
- UI色值高度浓缩，仅为7种，自定义超简单     
- 对Gif动图的处理（可配置开关是否显示，列表中以静态图+动图标识显示，预览大图时自动播放）    
- 自带Bitmap相关方法（如添加水印、把View画成Bitmap、保存Bitmap等）    
- 自带媒体库相关方法（如媒体文件更新到媒体库）    

## 关于EasyPhotos的SDK及相关版本公示（androidx版本） 
compileSdkVersion 29  
minSdkVersion 15  
targetSdkVersion 29      
QQ交流群：[288600953](https://jq.qq.com/?_wv=1027&k=5QGgCDe)      
[demo下载](https://raw.githubusercontent.com/HuanTanSheng/EasyPhotos/master/demo/release/demo-release.apk)     

## 关于EasyPhotos的SDK及相关版本公示（support版本） 
compileSdkVersion 28  
minSdkVersion 15  
targetSdkVersion 28      
buildToolsVersion '28.0.3'    
QQ交流群：[288600953](https://jq.qq.com/?_wv=1027&k=5QGgCDe)      



## 获取EasyPhotos（通过Gradle方式）
首先，在项目的 `build.gradle（project）` 文件里面添加:

```gradle
allprojects {
	repositories {  

        maven { url "https://jitpack.io" }
		
    }
}
```

最后，在你需要用到EasyPhotos的module中的 `build.gradle（module）` 文件里面添加：
```gradle
dependencies {  

        implementation 'com.github.HuanTanSheng:EasyPhotos:3.0.6' //androidx版本，支持android 10，永久维护
      
        //implementation 'com.github.HuanTanSheng:EasyPhotos:2.4.5' //还没有升级到androidx的同学可以用这个版本，但不支持android 10，暂停维护
   
}
```    
    
    


    
       
**如果你的 `android studio` 版本低于3.4.2版，有可能会打不开我的Demo，只需要修改Demo里面 `build.gradle（project）` 文件中的：**     

```gradle  

dependencies {
        classpath 'com.android.tools.build:gradle:3.4.2'
	//把3.4.2改成你对应的版本即可，如果不清楚对应版本可以看看你其他正常项目的这里是怎么写的  
	}

```    
    
## 关于混淆    
  
**EasyPhotos的混淆：**  
```pro  

-keep class com.huantansheng.easyphotos.models.** { *; }

```

    
    
  
## EasyPhotos文档总录    

## [EasyPhotos文档总录](https://github.com/HuanTanSheng/EasyPhotos/wiki)
* [01-关于权限](https://github.com/HuanTanSheng/EasyPhotos/wiki/01-%E5%85%B3%E4%BA%8E%E6%9D%83%E9%99%90)
* [02-相机与相册](https://github.com/HuanTanSheng/EasyPhotos/wiki/02-%E7%9B%B8%E6%9C%BA%E4%B8%8E%E7%9B%B8%E5%86%8C)    
* [03-拼图（单独使用）](https://github.com/HuanTanSheng/EasyPhotos/wiki/03-%E6%8B%BC%E5%9B%BE%EF%BC%88%E5%8D%95%E7%8B%AC%E4%BD%BF%E7%94%A8%EF%BC%89)      
* [04-图片添加水印](https://github.com/HuanTanSheng/EasyPhotos/wiki/04-%E5%9B%BE%E7%89%87%E6%B7%BB%E5%8A%A0%E6%B0%B4%E5%8D%B0)     
* [05-把View画成Bitmap](https://github.com/HuanTanSheng/EasyPhotos/wiki/05-%E6%8A%8AView%E7%94%BB%E6%88%90Bitmap)    
* [06-保存Bitmap到指定文件夹](https://github.com/HuanTanSheng/EasyPhotos/wiki/06-%E4%BF%9D%E5%AD%98Bitmap%E5%88%B0%E6%8C%87%E5%AE%9A%E6%96%87%E4%BB%B6%E5%A4%B9)    
* [07-Bitmap回收](https://github.com/HuanTanSheng/EasyPhotos/wiki/07-Bitmap%E5%9B%9E%E6%94%B6)
* [08-更新媒体文件到媒体库](https://github.com/HuanTanSheng/EasyPhotos/wiki/08-%E6%9B%B4%E6%96%B0%E5%AA%92%E4%BD%93%E6%96%87%E4%BB%B6%E5%88%B0%E5%AA%92%E4%BD%93%E5%BA%93)
* [09-屏幕方向设置](https://github.com/HuanTanSheng/EasyPhotos/wiki/09-%E5%B1%8F%E5%B9%95%E6%96%B9%E5%90%91%E8%AE%BE%E7%BD%AE)
* [10-自定义UI样式](https://github.com/HuanTanSheng/EasyPhotos/wiki/10-%E8%87%AA%E5%AE%9A%E4%B9%89UI%E6%A0%B7%E5%BC%8F)
* [11-多语言](https://github.com/HuanTanSheng/EasyPhotos/wiki/11-%E5%A4%9A%E8%AF%AD%E8%A8%80)      
* [12-配置ImageEngine，支持所有图片加载库](https://github.com/HuanTanSheng/EasyPhotos/wiki/12-%E9%85%8D%E7%BD%AEImageEngine%EF%BC%8C%E6%94%AF%E6%8C%81%E6%89%80%E6%9C%89%E5%9B%BE%E7%89%87%E5%8A%A0%E8%BD%BD%E5%BA%93)

    
QQ交流群：[288600953](https://jq.qq.com/?_wv=1027&k=5QGgCDe)    
    
       
           
	   
## 感谢    
     
[PhotoView](https://github.com/chrisbanes/PhotoView)：一个强大的图片缩放库，由[chrisbanes](https://github.com/chrisbanes) 大神编写    
    
[PuzzleView](https://github.com/wuapnjie/PuzzleView)：一个强大的拼图库，我的拼图功能是在此基础上实现，这个库由[wuapnjie](https://github.com/wuapnjie) 编写。    
    
## 编者语    

EasyPhotos将在高颜值、高兼容、高性能、强功能的道路上持续更新，欢迎各种Issues，我将及时反馈，谢谢！    
QQ交流群：[288600953](https://jq.qq.com/?_wv=1027&k=5QGgCDe)      


## 更新日志 
**3.0.6：**
- 优化：优化超出选择最大数的提示信息

**3.0.5：**
- 修复：fix #112
- 感谢：该版本由[XuQK](https://github.com/XuQK)贡献 

**3.0.4：**
- 优化：saveBitmapToDir方法兼容android10

**3.0.3：**
- 修复：修复华为nova 5i pro 在相机页面点击返回时产生的崩溃问题 #87

**3.0.2：**
- 重要：拼图和加水印功能适配android 10
- 感谢：该版本由[zhangshaobo87](https://github.com/zhangshaobo87)贡献 

**3.0.1：**
- 重要：兼容android 10
- 重要：因为android 10 不支持用path路径显示图片，所以回调取消了单独获取path集合的回调，只保留获取Photo集合的回调，如需使用path，可以在Photo对象中获取
- 重要：ImageEngine接口（因为android 10 不支持用path路径显示图片，所以全部改为Uri的形式），望升级用户周知

**2.5.2：** 
- 优化：修复ArrayList在多线程中addItem出现的角标越界问题

**2.4.9：** 
- 重要：升级到gradle:3.4.2，低版本studio可能因为该项升级而产生错误，建议升级studio或手动修改classpath 'com.android.tools.build:gradle:3.4.2'到你的可用版本
- 优化：修复预览视频封面为黑色图片问题 

**2.4.8：** 
- 优化：修复预览页预览大图片和长图片时清晰度模糊问题   

**2.4.7：** 
- 优化：修复相册页在部分机型会闪现权限提醒UI问题   

**2.4.6：** 
- 优化：支持androidx

**2.4.5：** 
- 修复：当相机按钮位置在图片第一张时，点击相册页底部中间的编辑按钮会导致右下角相机按钮也显示出来
- 修复：仅显示视频时，全部视频文件夹视频重复添加问题
- 修复：点击视频无法播放问题
- 感谢：该版本由[SMask](https://github.com/SMask)贡献 
   
**2.4.4：**     
- 修复：修复2.4.3版本引发的图片数据加载缓慢问题
- 感谢：该版本由[joker-fu](https://github.com/joker-fu)贡献
   
**2.4.3：**     
- 新增：start(SelectCallback callback)启动方式，通过接口回调数据
- 新增：filter(String... types)方式控制只显示的文件类型，支持Type.GIF和Type.VIDEO，前提是已经选择显示了gif和video
- 新增：对显示视频的时长过滤api
- 新增：单独对图片和视频的最大选择数控制
- 新增：支持相机按钮位置设置，setCameraLocation(@Setting.Location int cLocation)//默认左下角，通过设置可设置为相册第一张图片的位置    
- 优化：预览页
- 感谢：该版本由[joker-fu](https://github.com/joker-fu)、[SMask](https://github.com/SMask)贡献 
- 提示：新的api详见[wiki](https://github.com/HuanTanSheng/EasyPhotos/wiki/02-%E7%9B%B8%E6%9C%BA%E4%B8%8E%E7%9B%B8%E5%86%8C) 
   
**2.4.1：**     
- 优化：2.4.0中的代码
- 感谢：该版本由[joker-fu](https://github.com/joker-fu)、[SMask](https://github.com/SMask)贡献
        
**2.4.0：**     
- 感谢：该版本由[joker-fu](https://github.com/joker-fu)贡献
- 新增：视频选择功能    
- 新增API：是否启动视频选择，setVideo（boolean shouldShow）    
- 优化：默认不显示gif图，可通过setGif（boolean shouldShow）控制是否显示    

**2.3.6：**     
- 修复：2.3.5出现的拼图错误    
- 优化：解决部分机型在全屏预览图片返回到相册选择界面时状态栏闪烁的问题（感谢@wqxcloud）    
- 优化：相册UI（将原底部操作栏中间的设置按钮样式改为编辑样式）        
- 新增：相册页底部操作栏中间的编辑按钮，会根据开发者配置的清空按钮、原图按钮、拼图按钮使用情况进行显示或隐藏。（当清空按钮、原图按钮、拼图按钮都不显示时，编辑按钮隐藏。其余条件均显示。）    
- 新增：可配置相册页清空按钮是否显示（详见wiki）       
    
**2.3.5：**     
- 修复：修复文字贴纸自动生成日期错误    
- 优化：AlbumModel类，更加节省内存 （感谢@ofexe）   
- 升级：compileSdkVersion 升级为28，buildToolsVersion 升级为 '28.0.3'    
    
**2.3.4：**    
- 修复：坚果pro和荣耀8在特殊情况下无法获取媒体文件路径的问题        
    
**2.3.3：**    
- 优化：过滤媒体库中存在但实际不存在的图片    
    
**2.3.2：**    
- 优化：状态栏颜色独立为easy_photos_status_bar    
    

**2.3.1：**    
- 新增：调用相册支持Fragment直接调用，走Fragment的result回调    
- 修复：三星S3拍照错误     
- 修复：魅族用户在极端操作下的权限错误     
- 修复：app在后台时，因设备内存不足而回收资源后，在任务列表中启动app后的异常    
    
**2.3.0：**    
- 修复4.4.3YouTaPhone拍照时相机停止运行错误    
- 修复相册中没有图片情况下，EasyPhotos自动前往相机拍照时，发生的错误    

**2.2.9：**        
- 重要修改：Photo类中的time字段的单位改为毫秒    
- 优化：图片排序算法调整为与系统相册排序算法类似    
- 优化：内存泄露情况    
- 修复：部分机型出现少许图片丢失情况    
- 修复：部分机型不读扩展SD卡中照片的情况    
- 修复：三星的部分机型clearFilterColor（）方法无效的情况    
- 修复：三星的部分机型因在xml中绑定点击事件导致的点击无效的情况    
- 修改：单选选中图标改为数字1    
- 其他：demo中新增了内存泄露检测工具，如使用中发现内存泄露，麻烦告知，我将第一时间处理，感谢！


**2.2.8：**    
- bug修复：在预览页点击最后一张的选择无效，以及因此产生的数组越界bug    
- bug修复：极少情况下的预览页直接返回产生的空指针问题    
- 感谢@zijinzhiyun 反馈以上bug
    
    
**2.2.6：**    
- api修改：为统一api标准，将所有返回Key统一为EasyPhotos.RESULT_PHOTOS和EasyPhotos.RESULT_PATHS。带来的改变就是单独使用拼图功能时的图片返回Key改为EasyPhotos.RESULT_PHOTOS和EasyPhotos.RESULT_PATHS，去除原来的EasyPhotos.RESULT_PUZZLE_PHOTO和-
EasyPhotos.RESULT_PUZZLE_PATH这两个Key。      
- UI修改：预览页状态栏颜色与colorPrimaryDark色值对应。若其色值趋近于白色，在无虚拟按键的手机中状态栏字体颜色智能适配为深色，有虚拟按键的手机中状态栏智能优化为透明色。（除预览页外，其他页面若状态栏颜色趋近于白色，无论任何机型均为智能优化字体颜色为深色，[查看详情。](https://github.com/HuanTanSheng/EasyPhotos/wiki/10-%E8%87%AA%E5%AE%9A%E4%B9%89UI%E6%A0%B7%E5%BC%8F)）     
- 错误修复：修复努比亚机型的预览页占用导航栏问题      

**2.2.4：**    
- 优化：单独启动相机时无需配置图片加载引擎       
- 优化：示例中配置Glide4.x为图片加载引擎的示例文件改为单例模式    

**2.2.3：**    
- 修复拼一张更换图片时发生的错误    
- 修复单独启动相机时的权限错误    
- 优化混淆规则    

**2.2.2：**    
- 重大更新：EasyPhotos去除了Glide的依赖，并对外提供ImageEngine接口，通过对ImageEngine接口的实现，使用者可以快速绑定如Glide、picasso、fresco、Imageloader等你正在使用的任意图片加载库。[点击查看详情](https://github.com/HuanTanSheng/EasyPhotos/wiki/12-%E9%85%8D%E7%BD%AEImageEngine%EF%BC%8C%E6%94%AF%E6%8C%81%E6%89%80%E6%9C%89%E5%9B%BE%E7%89%87%E5%8A%A0%E8%BD%BD%E5%BA%93)    
- 界面优化：优化单选图标    
- 修改文字贴纸的文字编辑页面中，底部操作栏的背景色为固定色值（其实也可以改，看看EasyPhotos的color文件你就知道怎么改）

    
**2.1.0：**    
- 新增功能：EasyPhotos智能识别状态栏的背景颜色，当其趋近于白色时，智能适配深色状态栏字体。（该功能仅对6.0以上系统生效，并没有适配6.0以下的小米和魅族，如有需要可以加群交流）    
- 新增功能：可配置是否显示Gif动图    
- 功能优化：Gif动图的处理方式。
- 界面优化：Gif动图、相机按钮等。
- 新增字段：    
````java    
<string name="gif_easy_photos">动图</string>    
````    
具体查看[11-多语言](https://github.com/HuanTanSheng/EasyPhotos/wiki/11-%E5%A4%9A%E8%AF%AD%E8%A8%80)    

- 内部升级：最新版编译工具和最新版sdk

**2.0.2：**    
- 升级：Glide到4.5.0（不影响低版本使用）    
- 修复：拼一张功能因图片过多过大导致的oom问题    
- 感谢@[Beiler](https://github.com/beiler) 提出的反馈      
    
    
**2.0.1：**    
- 修复bug：单独使用拼图功能时，以图片路径为参数时产生的数组越界bug。   

**2.0.0：**   
- 新增功能：相册内部自带拼一张功能（可通过配置不使用该功能，该模式拼图保存的图片存储在 sd卡根目录/你的app_name 文件夹下）   
- 新增功能：拼图页面增加文字贴纸功能    
- 界面优化：无权限时的相册界面优化，使之更加友好    
- 界面优化：相机按钮优化，视觉和体验上都更加友好    
- 界面优化：专辑列表细节优化，更加自然、大气     
- 功能优化：相册界面打开相机，拍照后不直接返回，而是默认选中拍完的图片，停留在相册界面
- 字段增加：具体查看[11-多语言](https://github.com/HuanTanSheng/EasyPhotos/wiki/11-%E5%A4%9A%E8%AF%AD%E8%A8%80)      
- 重要修改：Photo对象去除isCamera成员变量。构造函数也因此相应改变，少了一个参数。
- 重要修改：setFileProviderAuthoritiesText（）方法更改为setFileProviderAuthority（）方法    
- 重要修改：UI色值重新定义，由原来的三十几种色值统一修改为7个色值，自定义起来更加方便。具体查看[10-自定义UI样式](https://github.com/HuanTanSheng/EasyPhotos/wiki/10-%E8%87%AA%E5%AE%9A%E4%B9%89UI%E6%A0%B7%E5%BC%8F)



**1.3.2：**   
- 新增功能：    
    - 拼图（最多对9张图片进行拼图，无需关心运行时权限，内部处理好了）    
    - 把View画成Bitmap    
    - 保存bitmap到本地（可设置是否更新到媒体库，如果调用此方法前没有进入过EasyPhotos的相册或相机，则需要你自己处理读写权限）      
- 更换相册单选的选中图标样式    
- 修复回调选中地址的key：RRESULT_PATHS 修复为 RESULT_PATHS。（ps：对如此智障的疏忽表示歉意。）    
- 修复永久不给权限情况下，退出相册时发生的错误    
- 修复调用系统权限设置页返回时，相册页面或拼图页面自销毁情况
- 新增字符串：    
```java    
    <string name="done_easy_photos">完成</string>
    <string name="cancel_easy_photos">取消</string>
    <string name="template_easy_photos">模板</string>    
```   
- 新增色值：    
```java   
    <!--图片预览页-->
    <color name="preview_status_easy_photos">#d73c3d41</color>//api21以上预览页状态栏颜色为该色值；api19和api20状态栏为透明色；其余api状态栏或透明或黑或灰，取决于各家rom和有无实体按键等因素。注：其余页面状态栏根据你的主题走

    <!--拼图页-->
    <color name="puzzle_background_easy_photos">#000000</color>//拼图页面背景色
    <color name="puzzle_selected_frame_easy_photos">#57a457</color>//拼图页面当前处理item的边框颜色
    <color name="puzzle_selected_controller_easy_photos">#00AA00</color>//拼图页面当前处理item的操作bar颜色，就是item边框中凸起矩形的色值
    <color name="puzzle_menu_easy_photos">#969696</color>//拼图页面的文字按钮和示例图片颜色
    <color name="puzzle_menu_done_easy_photos">#009700</color>//拼图页面的完成按钮文字颜色
    <color name="puzzle_bottom_bar_line_easy_photos">#ee3a3a3e</color>//拼图的底部栏间隔颜色
    <color name="puzzle_photo_background">#ffffff</color>//图片的背景颜色    
```
    
**1.2.8：**    
- 修复'选中图片列表'点击状态下与'大图列表和选择器'的联动错误    

**1.2.7：**    
- 大图预览页新增：大图列表与选中图片列表联动    
- 升级 classpath 'com.android.tools.build:gradle:3.0.1'    
- 新增色值：    
```java    
<color name="preview_bottom_bar_easy_photos">#eb212123</color>//预览页的底部栏和选中图片列表背景颜色    
<color name="preview_bottom_bar_line_easy_photos">#ee3a3a3e</color>//预览页的底部栏与选中图片列表的分割线颜色    
```   

**1.2.6：**    
- 正式开放，投入使用   
- 广告view可以传空，适用于VIP不显示广告场景

**1.2.3：**    
- 优化图片限制方式：最小宽度、最小高度、最小文件大小    
- 如果单一设置，满足条件即过滤    
- 如果多项设置，满足一项即过滤    

**1.2.2：**     
- 新增返回结果：图片地址集合   
- 新增返回结果：用户是否选中原图选项    
- 新增返回结果：图片信息集合    
- 新增设置默认勾选图片集合方式：图片地址集合   
- 新增设置默认勾选图片集合方式：图片信息集合   

**1.2.1：**   
- 优化预览界面全屏动效
- 优化Photo实体对象  

**1.2.0：**   
- 升级图片选取返回信息（图片地址/宽高/文件大小/文件修改时间/文件类型/用户是否点击原图选项/文件名）
- 图片选择新增原图选项
- 预览界面支持选择完成
- 预览界面支持当前图片位置显示   

**1.1.1：**   
- 优化相机和相册的调用API，使之更加友好    

**1.1.0：**   
- 增加图片添加水印功能  
- 增加媒体文件更新到媒体库功能

**1.0.9：**   
- 优化三星部分机型因图片更新到媒体库时没有更新宽高信息时EasyPhotos相册不显示该图片问题

**1.0.8：**   
- 优化自定义UI和多语言

**1.0.7：**   
- 性能优化

**1.0.6：**   
- 修复华为VNS-L31机型拍照无返回问题

**1.0.5：**   
- 修复拍照切换横竖屏发生内存泄漏
- 修复切换语言时产生错误
- 升级glide为最新版4.3.0   

**1.0.4：**    
- 直接启动相机  
- 相册单选  
- 相册多选  
- 相册中支持添加自定义广告  
- 图片预览（缩放/全屏）
- UI可定制  
- 根据图片宽高进行过滤 
- 修复无图片显示时的异常    
- 内部处理权限问题，无需配置，无需处理运行时权限
    
    
    

  

