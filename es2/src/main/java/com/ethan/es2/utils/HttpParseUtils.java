package com.ethan.es2.utils;

import com.ethan.es2.entity.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HttpParseUtils {

    public static void main(String[] args) throws IOException {

        HttpParseUtils.parseJD("1").forEach(System.out::println);
        System.out.println(HttpParseUtils.parseJD("1").size());
    }

    //解析京东java关键字搜索结果
    public static List<Content> parseJD(String keyword) throws IOException{

        //前提： 联网 ajax不能获取到数据， 只能模仿浏览器获取
        String url = "https://search.jd.com/Search?keyword="+keyword;

        //解析网页 document就是网页对象
        Document document = Jsoup.parse(new URL(url), 30000);

        Element element = document.getElementById("J_goodsList");

        //System.out.println(element.html());

        //获取所有的li元素
        Elements elements = element.getElementsByTag("li");

        //搜索结果
        ArrayList<Content> contents = new ArrayList<>();

        //获取元素中的内容
        elements.forEach(ele -> {

            //图片可能获取不到， 图片可能是延时加载的，带有source-data-lazy-img
            String imgUrl = ele.getElementsByTag("img").eq(0).attr("data-lazy-img");
            String price = ele.getElementsByClass("p-price").eq(0).text();
            String name = ele.getElementsByClass("p-name").eq(0).text();

            Content content = new Content();
            content.setImgUrl(imgUrl);
            content.setPrice(price);
            content.setName(name);

            contents.add(content);
        });

        return contents;
    }
}
