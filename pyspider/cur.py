# -*- coding: utf-8 -*-
"""
Created on 2019/4/1 17:20

@author: JiafengLiu

E-mail:jfliu_2017@stu.xidian.edu.cn

To : Work harder and harder, day day up! 
"""

import pycurl

c = pycurl.Curl()

c.setopt(pycurl.URL,'https://www.v2ex.com/go/')

c.setopt(pycurl.CAINFO,r'D:\python27\curl\curl-ca-bundle.crt')

c.perform()