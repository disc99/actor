package io.disc99.actor;

/**
 * 結果を通知するためのデータ構造
 */
public class Result<R> {
    public R result;
    public Exception error;
}
