package io.disc99.actor;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class SimpleActor<M, R> implements Runnable {

    private Queue<M>           messages = new ConcurrentLinkedQueue<M>();
    private List<Future<M, R>> futures  = new ArrayList<Future<M, R>>();

    Thread self = new Thread(this);

    private boolean isStateRunnable() {
        Thread.State state = self.getState();
        return state == Thread.State.NEW || state == Thread.State.TERMINATED;
    }

    /**
     * メッセージを送信する。
     * @param message
     */
    public void send(M message) {
        synchronized (self) {
            messages.add(message);
            if (self != null && !isStateRunnable()) {
                self.start();
            }
        }
    }

    /**
     * 結果予約を返す。
     * @param message
     * @return
     */
    public Future<M, R> sendToFuture(M message) {
        Future<M, R> future = new Future<M, R>(message);
        synchronized (self) {
            futures.add(future);
            messages.add(message);
            if (self != null && isStateRunnable()) {
                self.start();
            }
        }
        return future;
    }

    @Override
    public void run() {
        while(messages.size() > 0) {
            M message = messages.poll();
            Result<R> res = new Result<R>();
            try {
                R result = execute(message);
                res.result = result;
            } catch (Exception e) {
                error(message, e);
                res.error = e;
            }
            synchronized (self) {
                setResult(message, res);
            }
        }
    }

    /**
     * 実行結果を対応するイベントハンドラへ通知する。
     * @param message
     * @param result
     */
    private void setResult(M message, Result<R> result) {
        Future<M, R> target = null;
        for (Future<M, R> value : futures) {
            if (value.getMessage() == message) {
                target = value;
                break;
            }
        }

        // 該当するものがあれば結果を通知してリストから削除する
        if (target != null) {
            target.setResult(result);
            futures.remove(target);
        }
    }

    /**
     * このアクターの処理
     * @param message
     * @return
     */
    protected abstract R execute(M message);

    /**
     * 何か問題があった時にイベントを受け取るハンドラ
     * @param message
     * @param e
     */
    protected void error(M message, Exception e) {}
}
