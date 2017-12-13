#!/usr/bin/python
# -*- coding:utf-8 -*-


import requests
from bs4 import BeautifulSoup
import pandas as pd
from collections import defaultdict
from selenium import webdriver
from selenium.common.exceptions import NoSuchElementException
from selenium.webdriver.common.keys import Keys
import time
from tic_toc import tic, toc

tic()

trainfle = './data/train3.csv'
f1 = open(trainfle, 'w')

train_urls = ['https://music.douban.com/top250' ]
#
#
#
sel = webdriver.Chrome()
loginurl = 'https://music.douban.com/top250'
#open the login in page
sel.get(loginurl)

s = requests.session()
s.keep_alive = False
head = {'User-Agent': 'Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.1.6) Gecko/20091201 Firefox/3.5.6'}
for url in train_urls:
    try:
        response = requests.get(url, headers=head)
    except:
        continue
    soup = BeautifulSoup(response.text, 'lxml')
    # beautifulsoup 用来解析html然后提取内容
    print 'soup:', soup
    items = soup.find_all('div', class_='pl2')
    # 查找所有td标签
    print 'items:', items
    print("音乐排名" + "\n" + " 音乐名         作者      评分       评价人数 " )

    for item in items:
        field_name = item.find('a').get_text()
        # 获取td标签中的有a标签的所有文本信息
        field_zuozhe = item.find('p', class_='pl').get_text()
        # 获取td标签中的有p标签并且p标签内含有class_='p1'的所有文本信息
        field_pinfen = item.find('span', class_="rating_nums").get_text()
        field_pingjiarenshu = item.find('span', class_='pl').get_text()
        print  field_name+"        "  +  field_zuozhe  + "           " + field_pinfen + "    " + field_pingjiarenshu + "    "
        # 获取td标签中的所有文本信息
        f1.write(field_name.encode('utf-8').replace(' ', '').strip() + ','+ field_zuozhe.encode('utf-8').replace(',', ' ').strip() + ','+ field_pinfen.encode('utf-8').replace(',', ' ').strip() + ','+ field_pingjiarenshu.encode('utf-8').replace('(', ' ').replace(')','').strip() + '\n')
        # k = k + 1
        # if k % 7 == 0:
        #     f1.write(url.split('/')+'\n')
        #     # 如果有7个字符串，在最后加上它的航班号（第五个点的字符串）并且换一行
toc()
