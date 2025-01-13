// https://forum.image.sc/t/how-to-create-a-pixel-classification-thresholder-entirely-using-groovy-script-in-qupath/72333
// https://forum.image.sc/t/applying-automatic-threshold-method-in-qupath/88287/3
// https://forum.image.sc/t/script-for-generating-a-n-threshold-classifier-and-all-the-combinations-in-between/91912/6

import static qupath.lib.gui.scripting.QPEx.*
import qupath.lib.io.GsonTools
import qupath.lib.roi.ROIs
import qupath.lib.objects.PathObjects
import qupath.lib.regions.ImagePlane
import qupath.opencv.ops.ImageOps

import com.google.gson.Gson

// Script Arguments:                                [annotation filename, metadata filename, roi, min area, min hole area]
println "$args"

def annotationPath = args[0]
def metadataPath = args[1]

def roiCoords = args[2]                             

double minArea = args[3] as double
double minHoleArea = args[4] as double
double downsample = args[5] as double
int channel = args[6] as int
double threshold = args[7] as double

// Current Image:
def imageData = getCurrentImageData()
def server = imageData.getServer()

// Analyze Whole Image or ROI:                      if roiCoords == 0, use the whole image, else the entire image is processed
if (roiCoords != "0"){
    double left = roiCoords.tokenize(',')[0] as double
    double top = roiCoords.tokenize(',')[1] as double
    double w = roiCoords.tokenize(',')[2] as double
    double h = roiCoords.tokenize(',')[3] as double

    println  "ROI Coords: [$left, $top, $w, $h]"

    def roi = ROIs.createRectangleROI(left, top, w, h, ImagePlane.getPlane(0, 0))
    def annot = PathObjects.createAnnotationObject(roi)

    addObject(annot)
    selectObjects(annot)

}

// Create + Apply Thresholder:
println "Building Thresholder"

def thresholder = null

def pixelCal = server.getPixelCalibration()
def resolution = pixelCal.createScaledInstance(downsample, downsample)

def above = getPathClass('Ignore*')
def below = getPathClass('Tissue')

if (channel == 3){
    def classificationChannel = qupath.lib.images.servers.ColorTransforms.createMeanChannelTransform()
    def ops = [ ImageOps.Threshold.threshold(threshold) ]

    Map<Integer, PathClass> classifications = new LinkedHashMap<>()
    classifications.put(0, below)
    classifications.put(1, above)

    def op = ImageOps.Core.sequential(ops)
    def transformer = ImageOps.buildImageDataOp(classificationChannel).appendOps(op)

    thresholder = qupath.opencv.ml.pixel.PixelClassifiers.createClassifier( transformer, resolution, classifications )

}
else {
    thresholder = qupath.opencv.ml.pixel.PixelClassifiers.createThresholdClassifier(resolution, channel, threshold, below, above)

}

println "Running Thresholder"

createAnnotationsFromPixelClassifier(thresholder, minArea, minHoleArea)

// Export Annotations + Image:                      https://qupath.readthedocs.io/en/stable/docs/advanced/exporting_annotations.html
exportObjectsToGeoJson(getAnnotationObjects(), annotationPath, "FEATURE_COLLECTION")

// Export Image Metadata:
Gson gson = new Gson()
File txtfile = new File(metadataPath)

txtfile.write( gson.toJson(server.getMetadata()).toString() )