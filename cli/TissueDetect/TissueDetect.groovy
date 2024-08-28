// https://forum.image.sc/t/how-to-create-a-pixel-classification-thresholder-entirely-using-groovy-script-in-qupath/72333
import static qupath.lib.gui.scripting.QPEx.*
import qupath.lib.io.GsonTools
import qupath.lib.roi.ROIs
import qupath.lib.objects.PathObjects
import qupath.lib.regions.ImagePlane

// Script Arguments:
println "$args"
def annotationFilePath = args[1]
def json = new File(args[0]).text
def thresholder = GsonTools.getInstance(true).fromJson(json, qupath.lib.classifiers.pixel.PixelClassifier.class)

// Current Image:
def imageData = getCurrentImageData()
def server = imageData.getServer()

// Analyze Whole Image or ROI:
if ((args.length).is(3)){
    coords = args[2]
    double left = coords.tokenize(',')[0] as double
    double top = coords.tokenize(',')[1] as double
    double w = coords.tokenize(',')[2] as double
    double h = coords.tokenize(',')[3] as double

    println  "ROI Coords: [$left, $top, $w, $h]"

    def roi = ROIs.createRectangleROI(left, top, w, h, ImagePlane.getPlane(0, 0))
    def annot = PathObjects.createAnnotationObject(roi)

    addObject(annot)
    selectObjects(annot)

}

// Create + Apply Thresholder:
println "Running Thresholder"
double minArea = 0
double minHoleArea = 0

createAnnotationsFromPixelClassifier(thresholder, minArea, minHoleArea)

// Export Annotations + Image -- https://qupath.readthedocs.io/en/stable/docs/advanced/exporting_annotations.html
def annotations = getAnnotationObjects()
exportObjectsToGeoJson(annotations, annotationFilePath, "FEATURE_COLLECTION")
