/**
 * Diffy API module info
 */
open module com.wildbeeslabs.sensiblemtrics.diffy.comparator {
    requires static lombok;
    requires slf4j.api;
    requires jsr305;
    requires com.fasterxml.jackson.annotation;
    requires org.apache.commons.lang3;
    requires com.fasterxml.jackson.databind;
    requires commons.collections;
    requires com.google.common;
    requires java.sql;
    requires org.mongodb.bson;
    requires gson;
    requires com.fasterxml.jackson.core;
    requires commons.beanutils;
    requires java.desktop;
    requires com.fasterxml.classmate;
    requires commons.lang;
    requires org.jetbrains.annotations;
    requires org.apache.commons.text;
    requires com.fasterxml.jackson.dataformat.yaml;
    requires protonpack;
    requires org.apache.commons.codec;
    requires java.compiler;
    requires org.jsoup;
    requires commons.validator;
    requires com.wildbeeslabs.sensiblemtrics.diffy.common;
    requires com.wildbeeslabs.sensiblemtrics.diffy.matcher;

    // exports comparator factory
    exports com.wildbeeslabs.sensiblemetrics.diffy.comparator.factory;
    // exports comparator interfaces
    exports com.wildbeeslabs.sensiblemetrics.diffy.comparator.interfaces;
    // exports comparator service
    exports com.wildbeeslabs.sensiblemetrics.diffy.comparator.service;
    // exports comparator utils
    exports com.wildbeeslabs.sensiblemetrics.diffy.comparator.utils;
}
