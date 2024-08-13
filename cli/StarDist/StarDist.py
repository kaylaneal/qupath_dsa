import os
import logging
import json

import numpy as np
import large_image
from histomicstk.cli import utils

logging.basicConfig(level = logging.CRITICAL)

def main(args):
    print('\n *** CLI Parameters: *** \n')
    print(args)

    if not os.path.isfile(args.inputImageFile):
        raise OSError('Input image file does not exist.')
    
    if len(args.analysis_roi) != 4:
        raise ValueError('Analysis ROI must be a vector of 4 elements.')
    
    # print('\n *** Creating DASK Client *** \n')
    # client = utils.create_dask_client(args)

    # (-1, -1, -1, -1): Process full image. Else, process ROI
    process_roi = False if np.all(np.array(args.analysis_roi) == -1) else True

    # Read Input Image:
    print('\n *** Reading Input Image *** \n')

    inIMG = large_image.getTileSource(args.inputImageFile)
    img_meta = inIMG.getMetadata()

    print(json.dumps(img_meta, indent = 2))

    os.system(
        f"./../qpbin/QuPath-v0.5.1-Linux/QuPath/bin/QuPath \
            script StarDist/StarDist.groovy"
    )
     
if __name__ == "__main__":
    main(utils.CLIArgumentParser().parse_args())