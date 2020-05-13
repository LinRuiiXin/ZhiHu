package com.sz.zhihu.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlUtils {
    public static final Integer TYPE_ALL_TEXT = 1;
    public static final Integer TYPE_HAS_IMAGE = 2;
    public static final Integer TYPE_HAS_VIDEO = 3;
    /**
     * 获取html字符串中第一张图片的路径
     * @param htmlcontent
     * @return
     */
    public static String getImgFromHtml(String htmlcontent){
        if(htmlcontent!=null){
            String regEx_img = "<img.*src\\s*=\\s*(.*?)[^>]*?>";
            Pattern p_image = Pattern.compile(regEx_img,Pattern.CASE_INSENSITIVE);
            Matcher m_image = p_image.matcher(htmlcontent);
            if(m_image.find()){
                String img = m_image.group(0);
                Matcher m  = Pattern.compile("src\\s*=\\s*\"?(.*?)(\"|>|\\s+)").matcher(img);
                if(m.find()){
                    if(m.group(0)!=null){
                        return m.group(0).substring(5, m.group(0).length()-1);
                    }
                }
            }
        }
        return "";
    }
    /**
     * 获取html字符串中第一个视频的路径
     * @param htmlcontent
     * @return
     */
    public static String getVideoFromHtml(String htmlcontent){
        if(htmlcontent!=null){
            String regEx_img = "<video.*src\\s*=\\s*(.*?)[^>]*?>";
            Pattern p_image = Pattern.compile(regEx_img,Pattern.CASE_INSENSITIVE);
            Matcher m_image = p_image.matcher(htmlcontent);
            if(m_image.find()){
                String img = m_image.group(0);
                Matcher m  = Pattern.compile("src\\s*=\\s*\"?(.*?)(\"|>|\\s+)").matcher(img);
                if(m.find()){
                    if(m.group(0)!=null){
                        return m.group(0).substring(5, m.group(0).length()-1);
                    }
                }
            }
        }
        return "";
    }
    /**
     * 获取html字符串中的文字内容（去掉标签）
     * @param htmlcontent
     * @return
     */
    public static String getContentFromHtml(String htmlcontent){
        if(htmlcontent!=null){
            return htmlcontent.replaceAll("<\\/?.+?>", "");
        }
        return "";
    }

    public static Integer getContentType(String htmlString){
        Integer type = null;
        if(StringUtils.isEmpty(getVideoFromHtml(htmlString))){
            if(StringUtils.isEmpty(getImgFromHtml(htmlString))){
                type = TYPE_ALL_TEXT;
            }else{
                type = TYPE_HAS_IMAGE;
            }
        }else{
            type = TYPE_HAS_VIDEO;
        }
        return type;
    }
}
