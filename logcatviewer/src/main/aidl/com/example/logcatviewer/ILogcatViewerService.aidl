// ILogcatViewerService.aidl
package aidl.com.example.logcatviewer;

// Declare any non-default types here with import statements

interface ILogcatViewerService {

        void changeLogcatSource(String buffer);

        /**
         * Restart service.
         */
        void restart();

        /**
         * Stop service.
         */
        void stop();

        /**
         * Start saving logcat logs to given file for given filter-text.
         * File is stored in android.os.Environment.DIRECTORY_DOWNLOADS+ "/LogcatViewer/"+ getPackageName() directory.
         * @param logFilename file to which logs are saved.
         * @param filterText text by which logs should be filtered. It can be tag, package or some text.
         */
        void startRecording(String logFilename, String filterText);

        /**
         * Stop saving logcat logs.
         */
        void stopRecording();

        /**
         * Is 'saving logcat logs to file' active?
         * @return true if yes else false.
         */
        boolean isRecording();

        /**
         * Stop listening to logcat logs.
         */
        void pause();

        /**
         * Resume listening to logcat logs.
         */
        void resume();
}
