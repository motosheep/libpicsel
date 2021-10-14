# libpicselect

介绍
图片选择库，基于安卓原生实现的图片选择库

#### 使用说明


项目根目录build文件：需要加入

maven { url "https://jitpack.io" }




1.  图片加载配置


//图片加载库


        PicSelConfig.getInstance().setLoaderManager(this.getApplicationContext(), new PicSelConfig.BindImageViewListener() {
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

        //加载图片

        PicSelMain.getIntance().getPicSingle(false,MainActivity.this,true);

3.  数据回调配置

        PicSelMain.getIntance().ActivityForResult(requestCode, resultCode, data, new PicSelMain.PicCallbackListener() {
                    @Override
                    public void cameraResult(String path) {
                        Glide.with(MainActivity.this).load(path).into(img);
                    }

                    @Override
                    public void selectResult(ArrayList<String> result) {
                        Glide.with(MainActivity.this).load(result.get(0)).into(img);
                    }

                    @Override
                    public void cropResult(String path) {
                        Glide.with(MainActivity.this).load(path).into(img);
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











