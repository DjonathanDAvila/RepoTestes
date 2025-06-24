private static BitMatrix extractPureBits(BitMatrix image) throws NotFoundException {

    int height = image.getHeight();
    int width = image.getWidth();
    int minDimension = Math.min(height, width);

    // And then keep tracking across the top-left black module to determine module size
    //int moduleEnd = borderWidth;
    int[] leftTopBlack = image.getTopLeftOnBit();
    if (leftTopBlack == null) {
      throw NotFoundException.getNotFoundInstance();
    }
    int x = leftTopBlack[0];
    int y = leftTopBlack[1];
    while (x < minDimension && y < minDimension && image.get(x, y)) {
      x++;
    }
    if (x == minDimension) {
      throw NotFoundException.getNotFoundInstance();
    }

    int moduleSize = x - leftTopBlack[0];

    // And now find where the rightmost black module on the first row ends
    int rowEndOfSymbol = width - 1;
    while (rowEndOfSymbol >= 0 && !image.get(rowEndOfSymbol, y)) {
      rowEndOfSymbol--;
    }
    if (rowEndOfSymbol < 0) {
      throw NotFoundException.getNotFoundInstance();
    }
    rowEndOfSymbol++;

    // Make sure width of barcode is a multiple of module size
    if ((rowEndOfSymbol - x) % moduleSize != 0) {
      throw NotFoundException.getNotFoundInstance();
    }
    int dimension = 2 + ((rowEndOfSymbol - x) / moduleSize);

    y += moduleSize;

    // Push in the "border" by half the module width so that we start
    // sampling in the middle of the module. Just in case the image is a
    // little off, this will help recover.
    x -= moduleSize >> 1;
    y -= moduleSize >> 1;

    if ((x + (dimension - 1) * moduleSize) >= width ||
        (y + (dimension - 1) * moduleSize) >= height) {
      throw NotFoundException.getNotFoundInstance();
    }

    // Now just read off the bits
    BitMatrix bits = new BitMatrix(dimension);
    for (int i = 0; i < dimension; i++) {
      int iOffset = y + i * moduleSize;
      for (int j = 0; j < dimension; j++) {
        if (image.get(x + j * moduleSize, iOffset)) {
          bits.set(j, i);
        }
      }
    }
    return bits;
  }
