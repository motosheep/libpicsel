# libpicselect

介绍
图片选择库，基于安卓原生实现的图片选择库

#### 使用说明
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
