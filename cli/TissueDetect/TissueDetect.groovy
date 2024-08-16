// https://forum.image.sc/t/how-to-create-a-pixel-classification-thresholder-entirely-using-groovy-script-in-qupath/72333
import static qupath.lib.gui.scripting.QPEx.*
import qupath.lib.io.GsonTools
import qupath.lib.images.writers.ImageWriterTools
import qupath.lib.images.servers.LabeledImageServer

// Script Arguments:
println "$args"
def annotationFilePath = args[0]
def annotationImagePath = args[1]


// Current Image:
def imageData = getCurrentImageData()
def server = imageData.getServer()

// Thresholder Parameters:
int downsample = 8
int channel = 0
double threshold = 220

def above = getPathClass('Ignore*')
def below = getPathClass('Tissue')

double minArea = 0
double minHoleArea = 0

// Resolution
def cal = server.getPixelCalibration()
def res = cal.createScaledInstance(downsample, downsample)

// Create + Apply Thresholder:
def json = new File("../classifiers/tissueDetect.json").text
def thresholder = GsonTools.getInstance(true).fromJson(json, qupath.lib.classifiers.pixel.PixelClassifier.class)
// def thresholder = qupath.opencv.ml.pixel.PixelClassifiers.createThresholdClassifier(res, channel, threshold, below, above)
createAnnotationsFromPixelClassifier(thresholder, minArea, minHoleArea)

// Export Annotations + Image -- https://qupath.readthedocs.io/en/stable/docs/advanced/exporting_annotations.html
def annotations = getAnnotationObjects()
exportObjectsToGeoJson(annotations, annotationFilePath)

def labelServer = new LabeledImageServer.Builder(imageData)
    .backgroundLabel(0, ColorTools.WHITE)
    .downsample(downsample)
    .addLabel('Tissue', 1)
    .multichannelOutput(false)
    .build()
println "labelServer built"

ImageWriterTools.writeImage(labelServer, annotationImagePath)