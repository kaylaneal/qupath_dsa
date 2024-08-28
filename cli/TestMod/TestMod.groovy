import static qupath.lib.gui.scripting.QPEx.*
import qupath.lib.io.GsonTools
import qupath.lib.roi.ROIs
import qupath.lib.objects.PathObjects
import qupath.lib.objects.classes.PathClass
import qupath.lib.regions.ImagePlane


// Script Arguments:
int arg_count = args.length
println arg_count
println "$args"

def json = new File(args[0]).text
def thresholder = GsonTools.getInstance(true).fromJson(json, qupath.lib.classifiers.pixel.PixelClassifier.class)

// Load Image
def imageData = getCurrentImageData()
def server = imageData.getServer()

def pix_size = server.getPixelCalibration().getAveragedPixelSize()
println "Pixel Size: $pix_size"

// Check if Analysis is Whole Image or ROI:
if (arg_count.is(3)){
    coords = args[2]
    double left = coords.tokenize(',')[0] as double
    //left *= pix_size
    double top = coords.tokenize(',')[1] as double
    //top *= pix_size
    double w = coords.tokenize(',')[2] as double
    //w *= pix_size
    double h = coords.tokenize(',')[3] as double
    //h *= pix_size

    println "ROI Coords (pixel): [$left, $top, $w, $h]"

    def roi = ROIs.createRectangleROI(left, top, w, h, ImagePlane.getPlane(0, 0))

    double cx = roi.getCentroidX()
    double cy = roi.getCentroidY()
    println "ROI Centroid X um: $cx"
    println "ROI Centroid Y um: $cy"

    def annot = PathObjects.createAnnotationObject(roi)
    addObject(annot)
    selectObjects(annot)
}

println "Running Thresholder."
// Create + Apply Thresholder:
createAnnotationsFromPixelClassifier(thresholder, 0, 0)

// Export Annotations + Image -- https://qupath.readthedocs.io/en/stable/docs/advanced/exporting_annotations.html
def annotations = getAnnotationObjects()
exportObjectsToGeoJson(annotations, args[1], 'FEATURE_COLLECTION')