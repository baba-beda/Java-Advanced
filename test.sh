#!/bin/sh
java -cp artifacts/WebCrawlerTest.jar:out/production/Java-Advanced/:lib/hamcrest-core-1.3.jar:lib/junit-4.11.jar:lib/quickcheck-0.6.jar info.kgeorgiy.java.advanced.crawler.Tester easy ru.ifmo.ctddev.zenkova.crawler.WebCrawler
