## 找Bug记录

### 后端
- 返回的json文件中data数据写成了date

### OkHttp
- Android高版本拒绝了所有Http请求，也就是不安全的请求
- 后端只接受json数据，客户端这边老是传form过来
- okhttp不会在自动上传时携带sessionid，需要手动保存，在登录的状态下每次请求都发送sessionid给服务器确认。

### 业务
- 没网还用浏览器？
解决方案：
1. 用户信息保存为文件，退出登录时将文件删除，文件的存在与否可以在没网时判断登录状态
2. 把增删改操作存储到一个map/json文件，监听网络，通网时同步；方案更改为：无网时先删除本地记录，再存储删除书签的操作（该文件在用户登录时创建，在退出登录时删除，在同步删除成功后清空），用户点击同步时读取存储的操作对服务器数据进行删除。而新增和修改都会在用户点击同步时自动上传。对于bookmark的同步问题，所以只用比较url进行同步即可。
3. 一开始时，书签id在客户端和服务端上使用的都是自增id，但是这样会带来一个问题，那就是更换设备后，假如新设备未登录之前就有几条记录，然后再登录那就会导致和服务器记录冲突。举个例子，设备A有书签记录1、2、3、4（1234指的是id），而且同步好到服务器上，那么服务器就会有书签记录1、2、3、4；这时候新设备B出现，未登录就已经收藏了一条记录，那这个记录的id就是1，当登录时，会从服务器上拉去所有书签记录，服务器上的id为1的记录，和设备B上id为1的记录，实际上是不同的书签，就产生了冲突。为了保证不会出现这种情况，那对书签的记录id有一个要求，那就是不会出现重复的情况，所以用时间作为id最好
