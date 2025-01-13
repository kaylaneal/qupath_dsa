import os
import subprocess
import numpy as np
import json

from histomicstk.cli.utils import CLIArgumentParser

'''
Cell Detection
'''

def validate_args(args):
    if not os.path.isfile(args.input_image):
        raise OSError("Input image file does not exist.")
    elif len(args.analysis_roi) != 4:
        raise ValueError("Analysis ROI byt be a vector of 4 elements.")
    elif np.all( np.array(args.analysis_roi ) == -1 ):
        raise ValueError("Analysis cannot be done on whole image. Please select an ROI.")
    else:
        print("\n *** Input Arguments Validated *** \n")

def main(args):
    # Check Arguments:
    print("\n *** Parameters from CLI: *** \n", args)
    validate_args(args)

    # Define ROI:
    roi = ','.join(map( str, args.analysis_roi ))

    # Define Cell Detection Model:
    cd_model = {
        "detectionImageBrightfield":"Hematoxylin OD",
        "requestedPixelSizeMicrons":float(args.requestedPixelSize),
        "backgroundRadiusMicrons":float(args.backgroundRadius),
        "backgroundByReconstruction":str(args.backgroundByReconstruction),
        "medianRadiusMicrons":float(args.medianRadius),
        "sigmaMicrons":float(args.sigma),
        "minAreaMicrons":float(args.min_area),
        "maxAreaMicrons":float(args.max_area),
        "threshold":float(args.threshold),
        "maxBackground":float(args.maxBackground),
        "watershedPostProcess":"true",
        "cellExpansionMicrons":float(args.cellExpansion),
        "includeNuclei":"true",
        "smoothBoundaries":str(args.smoothBoundaries),
        "makeMeasurements":str(args.makeMeasurements)
    }

    # Run Cell Detection Script:
    print("\n *** Run QuPath Script: ***")

    subprocess.run([
        "./../qpbin/QuPath-v0.5.1-Linux/QuPath/bin/QuPath",
        "script", "CellDetection/CellDetection.groovy",
        "--image", args.input_image,
        "-a", args.outputAnnotationFile,
        "-a", roi,
        "-a", json.dumps(cd_model)
    ])


if __name__ == "__main__":
    main(CLIArgumentParser().parse_args())