package io.disc99.actor;

/**
 * Actor の処理が終わった際のコールバック
 */
public interface IFinishCallback<R> {
    void success(R result);
    void failed(Exception error);
}