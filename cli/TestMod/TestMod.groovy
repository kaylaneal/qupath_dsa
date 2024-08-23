import static qupath.lib.gui.scripting.QPEx.*
import qupath.lib.io.GsonTools
import qupath.lib.roi.RectangleROI
import qupath.lib.objects.PathAnnotationObject

// Script Arguments:
int arg_count = args.length
println arg_count
println "$args"

// Check if Analysis is Whole Image or ROI:
def roi = false
double left = 0
double top = 0
double w = 0
double h = 0

if (arg_count.is(3)){
    roi = args[2]
    left = roi.tokenize(',')[0] as double
    top = roi.tokenize(',')[1] as double
    w = roi.tokenize(',')[2] as double
    h = roi.tokenize(',')[3] as double
}
println left

// Load Image
def imageData = getCurrentImageData()
def server = imageData.getServer()

// Localize to ROI if Necessary:
if (roi != false){
    new RectangleROI(left, top, w, h)
}
selectAnnotations()

// Create + Apply Thresholder:
double minArea = 0
double minHoleArea = 0

def json = new File(args[0]).text
def thresholder = GsonTools.getInstance(true).fromJson(json, qupath.lib.classifiers.pixel.PixelClassifier.class)

createAnnotationsFromPixelClassifier(thresholder, minArea, minHoleArea)

// Export Annotations + Image -- https://qupath.readthedocs.io/en/stable/docs/advanced/exporting_annotations.html
def annotations = getAnnotationObjects()

exportObjectsToGeoJson(annotations, args[1])