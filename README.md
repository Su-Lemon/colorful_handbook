# 多彩手账

## 项目背景

大学生活有很多重要，精彩的事，但是只通过文字和图片来记录表达这些事情显然不够，我们的目的是制作一个包括音频视频的日记手账软件，能够更加便捷记录身边的事，同时能够让记录更加精彩。

## 运行说明

1. 启动服务器

   `cd .../socketserver` 

   `java SocketServerMain` 

   提示“wait for connection”则服务器启动成功

2. 注册运行“多彩手帐”APP

**注意**

- 服务器未启动时，仍可以本地注册登录APP，但是新建手账不会保存在服务端，不会在不同设备同步账号信息。
- 由于字体文件较大，上传时删除了部分字体。若有需要，可以自行下载（或从系统复制）`字体.ttf`文件到 `./application/app/src/main/assets/fonts/` 目录下，并自行修改部分 `Text` 相关的源码，以加载新添加的字体。

## 项目演示

### 主要功能展示

<table><tr>
<td><img src=images/1登录.png border=0></td>
<td><img src=images/2注册.png border=0></td>
<td><img src=images/3首页.png border=0></td>
<td><img src=images/4新建手账.png border=0></td>
</tr></table>

<table><tr>
<td><img src=images/5示例手账.png border=0></td>
<td><img src=images/6添加文本.png border=0></td>
<td><img src=images/7添加音频.png border=0></td>
<td><img src=images/8贴纸.png border=0></td>
</tr></table>

<video width="320" height="240" controls>   
	<source src="images/多彩手帐.mp4" type="video/mp4">  
  您的浏览器不支持Video标签。
</video>

### 示例手账

<table><tr>
<td><img src=images/5-1更多示例.png border=0></td>
<td><img src=images/5-2更多示例.png border=0></td>
<td><img src=images/5-3更多示例.png border=0></td>
</tr></table>

## 开发环境

- 最新的Android Studio集成开发环境
- IntelliJ IDEA集成开发环境

## 系统功能

![](images/用户故事.png)



