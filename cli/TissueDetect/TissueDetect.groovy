// https://forum.image.sc/t/how-to-create-a-pixel-classification-thresholder-entirely-using-groovy-script-in-qupath/72333
import static qupath.lib.gui.scripting.QPEx.*
import qupath.lib.io.GsonTools

// Script Arguments:
println "$args"
def annotationFilePath = args[0]

// Current Image:
def imageData = getCurrentImageData()
def server = imageData.getServer()

// Create + Apply Thresholder:
double minArea = 0
double minHoleArea = 0

def json = new File("../classifiers/tissueDetect.json").text
def thresholder = GsonTools.getInstance(true).fromJson(json, qupath.lib.classifiers.pixel.PixelClassifier.class)

createAnnotationsFromPixelClassifier(thresholder, minArea, minHoleArea)

// Export Annotations + Image -- https://qupath.readthedocs.io/en/stable/docs/advanced/exporting_annotations.html
def annotations = getAnnotationObjects()
exportObjectsToGeoJson(annotations, annotationFilePath)
