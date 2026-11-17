var onlyHighlighted = false;
var skipOptions     = true;
var onlyOptions     = false;

var searchOn    = true;
var searchTerms = new String[] {
    "/api",
    "/v3"
};

var hidePathsOn = true;
var hidePaths = new String[] {
    "/billing",
    "/videos"
};

var showExtOn = false;
var showExt   = new String[] { "asp", "aspx", "jsp", "php" };

var hideExtOn = true;
var hideExt   = new String[] { "js","mjs","map","gif","jpg","jpeg","png","webp","avif","ico","bmp","css","woff","woff2","ttf","eot","otf","svg","mp4","webm","mp3" };

var statusOn = false;
var status   = new boolean[] { true, true, true, true }; // 2xx..5xx

var mimeOn     = true;
var mimeJson   = true;
var mimeHtml   = true;
var mimeScript = true;
var mimeCss    = false;
var mimeXml    = true;
var mimeFlash  = true;
var mimeBinary = false;
var mimeText   = true;
var mimeImages = false;

// ==================== request data ====================
var req    = requestResponse.request();
var res    = requestResponse.response();
var method = req.method();
var url    = req.url();
var path   = req.pathWithoutQuery().toLowerCase();
var file   = path.substring(path.lastIndexOf('/') + 1);
var ext    = file.contains(".") ? file.substring(file.lastIndexOf('.') + 1) : "";

// ==================== filters ====================
if (skipOptions && method.equalsIgnoreCase("OPTIONS")) return false;
if (onlyOptions && !method.equalsIgnoreCase("OPTIONS")) return false;

if (searchOn && Arrays.stream(searchTerms).noneMatch(url::contains)) return false;

if (showExtOn && !Arrays.asList(showExt).contains(ext)) return false;
if (hideExtOn && Arrays.asList(hideExt).contains(ext))   return false;

if (hidePathsOn && Arrays.stream(hidePaths).anyMatch(path::startsWith)) return false;

if (onlyHighlighted && !requestResponse.annotations().hasHighlightColor()) return false;

if (res == null) return false;

if (statusOn) {
    int c = res.statusCode() / 100;
    if (c < 2 || c > 5 || !status[c - 2]) return false;
}

if (mimeOn) {
    var m = res.statedMimeType();
    var ok =
        (mimeJson   && m == MimeType.JSON) ||
        (mimeHtml   && m == MimeType.HTML) ||
        (mimeScript && m == MimeType.SCRIPT) ||
        (mimeCss    && m == MimeType.CSS) ||
        (mimeXml    && m == MimeType.XML) ||
        (mimeFlash  && m == MimeType.APPLICATION_FLASH) ||
        (mimeText   && m == MimeType.PLAIN_TEXT) ||
        (mimeBinary && (m == MimeType.APPLICATION_UNKNOWN || m == MimeType.UNRECOGNIZED || m == MimeType.AMBIGUOUS || m == MimeType.NONE)) ||
        (mimeImages && (m == MimeType.IMAGE_JPEG || m == MimeType.IMAGE_PNG || m == MimeType.IMAGE_GIF || m == MimeType.IMAGE_BMP || m == MimeType.IMAGE_UNKNOWN));
    if (!ok) return false;
}

return true;
