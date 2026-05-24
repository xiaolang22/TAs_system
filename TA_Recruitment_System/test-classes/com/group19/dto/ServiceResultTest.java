package com.group19.dto;

import com.group19.TestRunner;

/**
 * Unit tests for {@link ServiceResult}.
 *
 * @author Group19
 */
public class ServiceResultTest extends TestRunner {

    // ---- success() factory method ----

    public void testSuccessCreatesSuccessResult() {
        ServiceResult<String> result = ServiceResult.success("hello", "操作成功");
        assertTrue("success() should create a successful result", result.isSuccess());
    }

    public void testSuccessSetsCorrectMessage() {
        ServiceResult<String> result = ServiceResult.success("hello", "操作成功");
        assertEquals("操作成功", result.getMessage());
    }

    public void testSuccessSetsCorrectData() {
        ServiceResult<String> result = ServiceResult.success("hello", "操作成功");
        assertEquals("hello", result.getData());
    }

    public void testSuccessMessageCanBeNull() {
        ServiceResult<String> result = ServiceResult.success("data", null);
        assertNull(result.getMessage());
    }

    // ---- failure() factory method ----

    public void testFailureCreatesFailureResult() {
        ServiceResult<String> result = ServiceResult.failure("操作失败");
        assertFalse("failure() should create a failure result", result.isSuccess());
    }

    public void testFailureSetsCorrectMessage() {
        ServiceResult<String> result = ServiceResult.failure("操作失败");
        assertEquals("操作失败", result.getMessage());
    }

    public void testFailureDataIsNull() {
        ServiceResult<String> result = ServiceResult.failure("操作失败");
        assertNull("failure result data should be null", result.getData());
    }

    // ---- isSuccess() ----

    public void testIsSuccessReturnsTrueForSuccessResult() {
        ServiceResult<Integer> result = ServiceResult.success(42, "OK");
        assertTrue(result.isSuccess());
    }

    public void testIsSuccessReturnsFalseForFailureResult() {
        ServiceResult<Integer> result = ServiceResult.failure("错误");
        assertFalse(result.isSuccess());
    }

    // ---- getMessage() ----

    public void testGetMessageForSuccess() {
        ServiceResult<String> result = ServiceResult.success("data", "成功消息");
        assertEquals("成功消息", result.getMessage());
    }

    public void testGetMessageForFailure() {
        ServiceResult<String> result = ServiceResult.failure("失败消息");
        assertEquals("失败消息", result.getMessage());
    }

    // ---- getData() ----

    public void testGetDataForSuccessReturnsData() {
        ServiceResult<String> result = ServiceResult.success("测试数据", "OK");
        assertEquals("测试数据", result.getData());
    }

    public void testGetDataForFailureReturnsNull() {
        ServiceResult<String> result = ServiceResult.failure("错误");
        assertNull(result.getData());
    }

    // ---- Generic type tests ----

    public void testGenericWithString() {
        ServiceResult<String> result = ServiceResult.success("字符串数据", "OK");
        assertTrue(result.isSuccess());
        assertEquals("字符串数据", result.getData());
        assertEquals("OK", result.getMessage());
    }

    public void testGenericWithInteger() {
        ServiceResult<Integer> result = ServiceResult.success(100, "数字OK");
        assertTrue(result.isSuccess());
        assertEquals(100, result.getData());
        assertEquals("数字OK", result.getMessage());
    }

    public void testGenericWithNullData() {
        ServiceResult<String> result = ServiceResult.success(null, "数据可为空");
        assertTrue(result.isSuccess());
        assertNull(result.getData());
    }

    public void testGenericFailureTypeDoesNotMatter() {
        // failure data is always null, generic type can be any type
        ServiceResult<Integer> result = ServiceResult.failure("类型无关");
        assertFalse(result.isSuccess());
        assertNull(result.getData());
    }

    public static void main(String[] args) {
        new ServiceResultTest().runTests();
    }
}
