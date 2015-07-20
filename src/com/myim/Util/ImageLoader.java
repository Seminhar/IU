package com.myim.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.ListView;
import com.example.IU.R;
import com.myim.Adapter.MyChatAdapter;
import com.myim.Beans.ChatMessage;
import com.myim.NetService.Constant;
import com.myim.NetService.HttpFileUpload;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by PC on 2015-07-09.
 */
public class ImageLoader {

    private LruCache<String , Bitmap> imageLruCache;
    private Context context;
    private ListView listView ;
    private Set<LoadImageAsyn> tasks;
    public ImageLoader(Context context, ListView listView)
    {
        this.context = context ;
        this.listView = listView ;
        tasks = new HashSet<>();
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int size = maxMemory / 4 ;
        imageLruCache = new LruCache<String , Bitmap>(size)
        {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };


    }

    public void addBitmapToCache (String key,Bitmap bitmap)
    {
        if (getBitmapFromCache(key)==null)

            imageLruCache.put(key,bitmap);
    }
    public Bitmap getBitmapFromCache(String key)
    {
        return imageLruCache.get(key);
    }
    public Bitmap getBitmapFromLocal(String subFolder,String fileName)
    {
        String location  = SysStorageUtil.getStorageLocation(context);
        String pathName = location + "/" + subFolder + "/" + fileName;//????›¥¡¤??
        return BitmapUtil.getBitmapFromLocal(pathName);
    }


    public void saveBitmapToLocal(Bitmap bitmap,String subFolder,String url)
    {
        String fileName = new File(url).getName();
        String location  = SysStorageUtil.getStorageLocation(context);
        String pathName = location + "/" + subFolder + "/" + fileName;//????›¥¡¤??
        BitmapUtil.saveBitmapToLocal(pathName, bitmap);
    }

    /**
     * ?????????
     * @param start ??
     * @param end ??
     * @param subFolder ???›Ô?????
     */
    public void showImageWithRange(int start , int end,String subFolder )
    {

        for(int i =start ; i< end;i++)
        {
            ChatMessage msg = MyChatAdapter.allContents.get(i);
            String mime = msg.getMime();

            //?????????????????§Þ???
            if(mime.equals(ChatMessage.IMG_MIME)) {
                String url = msg.getContent();
                String fileName = new File(url).getName();
                Bitmap bitmap = getBitmapFromCache(url);
                if (bitmap == null) {

                    bitmap = getBitmapFromLocal(subFolder, fileName);
                    if (bitmap != null) {
                        addBitmapToCache(url, bitmap);
                        ImageView imgV = (ImageView) listView.findViewWithTag(url);
                        imgV.setImageBitmap(bitmap);

                    }
                    else {

                        // LoadImageAsyn loadImageAsyn = new LoadImageAsyn(imgV,url,subFolder);
                        url = Constant.HTTP_HOST+"thumbnail/"+fileName ;
                        LoadImageAsyn loadImageAsyn = new LoadImageAsyn(url, subFolder);
                        loadImageAsyn.execute(url);
                        tasks.add(loadImageAsyn);
                    }
                } else {
                    ImageView imgV = (ImageView) listView.findViewWithTag(url);
                    imgV.setImageBitmap(bitmap);
                }
            }
        }
    }
    public void stopAllTasks()
    {
        if(tasks!=null)
        {
            for(LoadImageAsyn l : tasks)
            {
                l.cancel(false);
            }
        }
    }
    public void showImage(ImageView imgV,String url,String subFolder)

    {
        Bitmap bitmap = getBitmapFromCache(url) ;
        if(bitmap==null)
        {
            String fileName = new File(url).getName();
            bitmap  = getBitmapFromLocal(subFolder,fileName);
            if(bitmap!=null)
            {
                addBitmapToCache(url,bitmap);
                imgV.setImageBitmap(bitmap);

            }
            else
            {
                imgV.setImageResource(R.drawable.bg_image);
//
//                LoadImageAsyn loadImageAsyn = new LoadImageAsyn(imgV,url,subFolder);
//                loadImageAsyn.execute(url);
            }
        }
        else
        {
            imgV.setImageBitmap(bitmap);
        }
    }


    class LoadImageAsyn extends AsyncTask<String ,Void,Bitmap>
    {


        private String url,subFolder;

        public LoadImageAsyn(String url,String subFolder)
        {

            this.url = url ;
            this.subFolder = subFolder ;
        }
        @Override
        protected Bitmap doInBackground(String... params) {

            String path = HttpFileUpload.download(params[0],Constant.THUMBNAIL_DIR,context);
            Bitmap bitmap = BitmapUtil.getBitmapFromLocal(path);
            //Bitmap bitmap = BitmapUtil.getBitmapFromUrl(params[0]);
            if(bitmap!=null)
            {
                addBitmapToCache(params[0],bitmap);
                // ????????????
//                if(subFolder!=null)
//                    saveBitmapToLocal(bitmap,subFolder,params[0]);
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            ImageView imgV = (ImageView) listView.findViewWithTag(url);
            if(imgV!=null && bitmap!=null ) {
                imgV.setImageBitmap(bitmap);

            }
            tasks.remove(this);

        }

    }
}
