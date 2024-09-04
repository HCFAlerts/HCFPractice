package me.hcfalerts.practice.tablist.impl;


public class TabListThread extends Thread {

    private final TabList ziggurat;

    public TabListThread(TabList ziggurat) {
        this.ziggurat = ziggurat;
    }

    @Override
    public void run() {
        while(true) {
            try {
                for (GhostlyTablist value : ziggurat.getTablists().values()) {
                    value.update();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                sleep(ziggurat.getTicks() * 50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
