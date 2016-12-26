package io.disc99.actor;

public class Main {
    public static void main(String[] args) throws Exception {
        // 上記 1 に該当
        SimpleActor<String, String> sampleActor = new SimpleActor<String, String>() {

            // 上記の 4 に該当。
            @Override
            protected String execute(String message) {
                return "Hello! mr," + message;
            }
        };

        // 上記 2 の処理。Future は非同期に値を取得するための予約チケットみたいなもの
        Future<String, String> future = sampleActor.sendToFuture("White - azalea");

        // 他にすることもないので処理完了待ち。
        // (この程度の処理ならこのループが回ることはほぼ無いが、、、)
        while(!future.isFinished()) {
            Thread.sleep(10);
        }

        System.out.println("Finish : " + future.getResult().result);
    }
}
