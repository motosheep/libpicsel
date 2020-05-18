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


//--------------------------------------------------------------------------------------------------
 20200516版本更新：

 1修改界面的视觉

 2修改图片图片浏览详情，允许选择图片


