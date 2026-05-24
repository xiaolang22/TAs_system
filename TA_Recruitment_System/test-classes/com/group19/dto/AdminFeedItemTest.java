package com.group19.dto;

import com.group19.TestRunner;

/**
 * Unit tests for {@link AdminFeedItem}.
 *
 * @author Group19
 */
public class AdminFeedItemTest extends TestRunner {

    // ---- Default constructor ----

    public void testDefaultConstructorCreatesObject() {
        AdminFeedItem item = new AdminFeedItem();
        assertNotNull("default constructor should create a non-null object", item);
    }

    public void testDefaultConstructorFieldsAreNull() {
        AdminFeedItem item = new AdminFeedItem();
        assertNull("title should be null after default constructor", item.getTitle());
        assertNull("meta should be null after default constructor", item.getMeta());
    }

    // ---- Parameterised constructor ----

    public void testParameterisedConstructorSetsTitle() {
        AdminFeedItem item = new AdminFeedItem("新职位发布", "刚刚");
        assertEquals("新职位发布", item.getTitle());
    }

    public void testParameterisedConstructorSetsMeta() {
        AdminFeedItem item = new AdminFeedItem("新职位发布", "刚刚");
        assertEquals("刚刚", item.getMeta());
    }

    public void testParameterisedConstructorSetsBothFields() {
        AdminFeedItem item = new AdminFeedItem("申请已审核", "2小时前");
        assertEquals("申请已审核", item.getTitle());
        assertEquals("2小时前", item.getMeta());
    }

    public void testParameterisedConstructorWithNullTitle() {
        AdminFeedItem item = new AdminFeedItem(null, "meta值");
        assertNull("title may be null", item.getTitle());
        assertEquals("meta值", item.getMeta());
    }

    public void testParameterisedConstructorWithNullMeta() {
        AdminFeedItem item = new AdminFeedItem("title值", null);
        assertEquals("title值", item.getTitle());
        assertNull("meta may be null", item.getMeta());
    }

    // ---- getter / setter ----

    public void testSetAndGetTitle() {
        AdminFeedItem item = new AdminFeedItem();
        item.setTitle("系统通知");
        assertEquals("系统通知", item.getTitle());
    }

    public void testSetAndGetMeta() {
        AdminFeedItem item = new AdminFeedItem();
        item.setMeta("3天前");
        assertEquals("3天前", item.getMeta());
    }

    public void testSetTitleOverwrites() {
        AdminFeedItem item = new AdminFeedItem("旧标题", "旧meta");
        item.setTitle("新标题");
        assertEquals("新标题", item.getTitle());
        assertEquals("旧meta", item.getMeta()); // meta remains unchanged
    }

    public void testSetMetaOverwrites() {
        AdminFeedItem item = new AdminFeedItem("标题", "旧meta");
        item.setMeta("新meta");
        assertEquals("标题", item.getTitle()); // title remains unchanged
        assertEquals("新meta", item.getMeta());
    }

    public void testSetTitleToNull() {
        AdminFeedItem item = new AdminFeedItem("标题", "meta");
        item.setTitle(null);
        assertNull(item.getTitle());
    }

    public void testSetMetaToNull() {
        AdminFeedItem item = new AdminFeedItem("标题", "meta");
        item.setMeta(null);
        assertNull(item.getMeta());
    }

    public void testFullLifecycle() {
        // Full lifecycle test: default constructor -> set -> get -> overwrite -> get
        AdminFeedItem item = new AdminFeedItem();
        assertNull(item.getTitle());
        assertNull(item.getMeta());

        item.setTitle("通知1");
        item.setMeta("5分钟前");
        assertEquals("通知1", item.getTitle());
        assertEquals("5分钟前", item.getMeta());

        item.setTitle("通知2");
        item.setMeta("10分钟前");
        assertEquals("通知2", item.getTitle());
        assertEquals("10分钟前", item.getMeta());
    }

    public static void main(String[] args) {
        new AdminFeedItemTest().runTests();
    }
}
