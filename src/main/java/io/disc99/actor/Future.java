package io.disc99.actor;


public class Future<M, R> {

    private M message = null;

    public Future(M message) {
        this.message = message;
    }

    public M getMessage() {
        return this.message;
    }

    private Result<R> result = null;

    /**
     * 処理が終わったかどうか
     * @return
     */
    public boolean isFinished() {
        synchronized (this) {
            return result != null;
        }
    }

    /**
     * 処理結果を取得する。
     */
    public Result<R> getResult() throws Exception {
        if (this.result == null)
            throw new IllegalAccessException("Item does not initialised.");
        return this.result;
    }

    /**
     * 結果を記録する
     * @param r
     */
    void setResult(Result<R> r) {
        synchronized (this) {
            this.result = r;
        }
        executeCallback();
    }

    private IFinishCallback<R> callback = null;

    /**
     * 成功・失敗時のコールバックを設定する。
     * @param callback
     */
    public void setCallback(IFinishCallback<R> callback) {
        synchronized (this) {
            this.callback = callback;

            // 既に成功済みなら実行する
            if (this.result != null)
                executeCallback();
        }
    }

    private void executeCallback() {
        synchronized (this) {
            if (this.callback != null) {
                if (this.result.error != null) {
                    this.callback.failed(this.result.error);
                } else {
                    this.callback.success(this.result.result);
                }
            }
        }
    }
}