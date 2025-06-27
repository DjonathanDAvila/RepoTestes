public static void main(String[] args) throws Exception {
    if (args.length == 0) {
      printUsage();
      return;
    }

    BarcodeFormat barcodeFormat = DEFAULT_BARCODE_FORMAT;
    String imageFormat = DEFAULT_IMAGE_FORMAT;
    String outFileString = DEFAULT_OUTPUT_FILE;
    int width = DEFAULT_WIDTH;
    int height = DEFAULT_HEIGHT;
    String contents = null;

    for (String arg : args) {
      String[] argValue = arg.split("=");
      switch (argValue[0]) {
        case "--barcode_format":
          barcodeFormat = BarcodeFormat.valueOf(argValue[1]);
          break;
        case "--image_format":
          imageFormat = argValue[1];
          break;
        case "--output":
          outFileString = argValue[1];
          break;
        case "--width":
          width = Integer.parseInt(argValue[1]);
          break;
        case "--height":
          height = Integer.parseInt(argValue[1]);
          break;
        default:
          if (arg.startsWith("-")) {
            System.err.println("Unknown command line option " + arg);
            printUsage();
            return;
          }
          contents = arg;
          break;
      }
    }

    if (contents == null) {
      printUsage();
      return;
    }
    
    if (DEFAULT_OUTPUT_FILE.equals(outFileString)) {
      outFileString += '.' + imageFormat.toLowerCase(Locale.ENGLISH);
    }
    
    BitMatrix matrix = new MultiFormatWriter().encode(contents, barcodeFormat, width, height);
    MatrixToImageWriter.writeToPath(matrix, imageFormat, Paths.get(outFileString));
  }
