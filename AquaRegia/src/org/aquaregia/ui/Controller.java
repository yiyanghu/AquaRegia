package org.aquaregia.ui;

import java.util.Date;
import com.google.bitcoin.core.DownloadListener;

public class Controller {
	
	/**
	 * Returns new blockchain download listener that updates UI on progress
	 */
    public class UIDownloadListener extends DownloadListener {
        @Override
        protected void progress(double pct, int blocksSoFar, Date date) {
            super.progress(pct, blocksSoFar, date);
            // TODO:  update progress bar UI element with new percentage
            // ensure bar is unhidden?

        }

        @Override
        protected void doneDownload() {
            super.doneDownload();
            // TODO: show download is done (maybe hide progress bar)
        }
    }

}
