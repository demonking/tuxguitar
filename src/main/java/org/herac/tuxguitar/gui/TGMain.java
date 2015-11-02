package org.herac.tuxguitar.gui;

import org.herac.tuxguitar.gui.tools.custom.converter.TGConverter;
import org.herac.tuxguitar.gui.tools.custom.converter.TGConverterFormat;
import org.herac.tuxguitar.gui.tools.custom.converter.TGConverterListener;
import org.herac.tuxguitar.io.base.*;
import org.herac.tuxguitar.io.gtp.GP5OutputStream;
import org.herac.tuxguitar.io.gtp.GTPSettings;

public class TGMain {

  public static void main(String[] args) {

    if (args.length > 0 && "--convert".equals(args[0])) {
      System.out.println("Console batch Convert");
      if(args.length != 4) {
        System.out.println("Invalid parameter count");
        System.exit(-1);
      }

      String extension = args[3];

      // TODO: This is a hack ;)
      TGFileFormatManager.instance().addOutputStream(new GP5OutputStream(new GTPSettings()));

      Object exporter = null;
      TGConverter converter = new TGConverter(args[1], args[2]);
      for(TGOutputStreamBase stream: TGFileFormatManager.instance().getOutputStreams()) {
        System.out.println(stream.getFileFormat().getSupportedFormats());
        if(stream.getFileFormat().getSupportedFormats().indexOf(extension) > 0) {
          exporter = stream;
          break;
        }
      }


      if(exporter == null) {
        for (final TGRawExporter ex : TGFileFormatManager.instance().getExporters()) {

          if (ex instanceof TGLocalFileExporter) {
            System.out.println(((TGLocalFileExporter) ex).getFileFormat().getSupportedFormats());
            exporter = ex;
          }
        }
      }

      converter.setFormat(new TGConverterFormat("." + extension, exporter));
      converter.setListener(new TGConverterListener() {
        @Override
        public void notifyFileProcess(String filename) {
          System.out.println("Processing: " + filename);
        }

        @Override
        public void notifyFileResult(String filename, int errorCode) {
          System.out.println("Error: " + errorCode + " " + filename);
        }

        @Override
        public void notifyFinish() {
          System.out.println("Fnished");
        }

        @Override
        public void notifyStart() {
          System.out.println("Start");
        }
      });
      converter.process();
    } else {
      TuxGuitar.instance().displayGUI(args);
      System.exit(0);
    }
  }

}
