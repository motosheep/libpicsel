# libpicselect

介绍
图片选择库，基于安卓原生实现的图片选择库

#### 使用说明


项目根目录build文件：需要加入

maven { url "https://jitpack.io" }




1.  图片加载配置

        //初始化
        PicSelMain.getInstance().init(this.getApplicationContext(), new PicSelConfig.BindImageViewListener() {
            @Override
            public void BindImageView(String path, ImageView iv) {
                Glide.with(MainActivity.this.getApplicationContext()).load(path).into(iv);
            }

            @Override
            public void BindSmallImageView(String path, ImageView iv) {
                Glide.with(MainActivity.this.getApplicationContext()).load(path).into(iv);
            }
        });



2.  启动选择页面配置

 findViewById(R.id.activity_main_choose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //加载图片
                String path = "/storage/emulated/0/Android/data/com.north.light.picsel/files/lib_pic_sel/camera/1649036977293.jpg";
                String path2 = "/storage/emulated/0/DCIM/Camera/VID_20220404_095827.mp4";
//                PicSelMain.getInstance().getPic(MainActivity.this, true, 3,
//                        true, true, true, true);
                //剪裁/storage/emulated/0/Android/data/com.north.light.picsel/files/lib_pic_sel/camera/1649036977293.jpg
//                PicSelMain.getInstance().cropPic(MainActivity.this,
//                        path,false,1,1);
//                PicSelMain.getInstance().recordVideo(MainActivity.this,20);
//                List<String> brList = new ArrayList<>();
//                brList.add(path);
//                brList.add(path2);
//                PicSelMain.getInstance().browsePic(brList, MainActivity.this, 0, 2);

            }

3.  数据回调配置

        //若调用此方法，记得及时释放，监听实例数量会作为是否交由外部处理的依据
        PicSelMain.getInstance().setActionListener(new PicActionListener() {
            @Override
            public void cusVideoPlay(Activity activity, String path) {

            }

            @Override
            public void cusCameraTake(Activity activity) {

            }
        });

        //拍摄回调
        PicSelMain.getInstance().setPicCallBackListener(new PicCallbackListener() {
            @Override
            public void cameraResult(String path) {

            }

            @Override
            public void selectResult(ArrayList<String> result) {

            }

            @Override
            public void cropResult(String path) {

            }

            @Override
            public void recordVideoPath(String path) {

            }
        });

//-----------------------------------------------------------------------------------
 
 
 20200516版本更新：

 1修改界面的视觉

 2修改图片图片浏览详情，允许选择图片




//-----------------------------------------------------------------------------------
 
 
 20200609版本更新：

 1修改图片浏览点击逻辑


 2增加视频录制入口


 PicSelMain.getIntance().recordVideo(this, 10);




 //---------------------------------------------------------------------------------
  
  
  20200824版本更新：

  1增加可选本地视频方法


  2增加视频浏览逻辑



 //---------------------------------------------------------------------------------
  
  
  20201021版本更新：

  1增加全屏浏览页面选择图片逻辑



//--------------------------------------------------------------------------------
 
 
 20201116版本更新：

 1增加初始化时，自动删除老旧图片逻辑
 2修改选择图片时，子线程获取数据
 3适配特殊字符图片选择时错误问题
 4修改保存图片的逻辑


//--------------------------------------------------------------------------------
  
  
  20210809版本更新：

1增加文件存在判断
2跳转选择图片视觉
3规范化资源文件调用


//--------------------------------------------------------------------------------
 
 
 20210930版本更新：

1增加网络视频跳转三方应用播放逻辑
2增加播放网络视频功能--playNetVideo
3取消初始化时，资源清理逻辑


//--------------------------------------------------------------------------------
  
  
  20211014版本更新：

1修复浏览资源时，视频资源播放按钮失效bug


//--------------------------------------------------------------------------------


  20211113版本更新：

1修复大量图片时，拍摄图片失效问题


//--------------------------------------------------------------------------------


  20211221版本更新：

1增加外部设置存储路径方法（适配安卓11）


2增加图片剪裁功能


//--------------------------------------------------------------------------------


  20220214版本更新：

1优化图片选择获取结果逻辑


//--------------------------------------------------------------------------------


  202200404版本更新：

1重构图库逻辑
2增加图库内部视频播放页面
3增加图库内部图片拍摄页面
4增加外部回调自定义图片拍摄，视频播放回调










