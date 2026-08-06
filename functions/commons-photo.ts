/**
 * Wikimedia Commons photo search for location (museum / dig site) hero images.
 * Searches by place name, returns PD/CC-licensed thumbnail URLs.
 * No API key required; respects Commons rate limits.
 */
export interface CommonsPhotoResult {
  url: string;
  license: string;
  filename: string;
  description: string;
}

interface CommonsSearchItem {
  title: string;
  snippet?: string;
}

interface CommonsImageInfo {
  url?: string;
  thumburl?: string;
  mime?: string;
  extmetadata?: {
    LicenseShortName?: { value: string };
    ImageDescription?: { value: string };
  };
}

const FREE_LICENSE_KEYWORDS = [
  "public domain",
  "pd",
  "cc-by",
  "cc-by-sa",
  "cc0",
  "gfdl",
];

function stripHtml(text: string): string {
  return text
    .replace(/<[^>]+>/g, "")
    .replace(/&amp;/g, "&")
    .replace(/&lt;/g, "<")
    .replace(/&gt;/g, ">")
    .replace(/&quot;/g, '"')
    .replace(/&#39;/g, "'")
    .trim();
}

export async function handleCommonsPhoto(
  request: Request,
  cors: Record<string, string>,
): Promise<Response> {
  try {
    const url = new URL(request.url);
    const query = url.searchParams.get("q") || "";
    const limit = Math.min(parseInt(url.searchParams.get("limit") || "5", 10), 10);

    if (query.trim().length < 2) {
      return Response.json(
        { error: "Query must be at least 2 characters" },
        { status: 400, headers: cors },
      );
    }

    // Step 1: Search Commons for files matching the query
    const searchUrl = new URL("https://commons.wikimedia.org/w/api.php");
    searchUrl.searchParams.set("action", "query");
    searchUrl.searchParams.set("format", "json");
    searchUrl.searchParams.set("list", "search");
    searchUrl.searchParams.set("srsearch", `${query} filetype:bitmap`);
    searchUrl.searchParams.set("srnamespace", "6");
    searchUrl.searchParams.set("srlimit", String(limit));
    searchUrl.searchParams.set("srprop", "snippet");

    const searchRes = await fetch(searchUrl.toString(), {
      headers: {
        "User-Agent": "RockScout/1.0 (support@rockscout.app)",
        "Accept-Language": "en-US",
      },
    });

    if (!searchRes.ok) {
      return Response.json(
        { error: "Commons search unavailable" },
        { status: 503, headers: cors },
      );
    }

    const searchData = await searchRes.json() as {
      query?: { search?: CommonsSearchItem[] };
    };
    const searchItems = searchData.query?.search || [];

    if (searchItems.length === 0) {
      return Response.json({ results: [] }, { headers: cors });
    }

    // Step 2: Get image info (URL, license, mime) for each found file
    const titles = searchItems
      .map((item) => item.title)
      .slice(0, limit)
      .join("|");

    const infoUrl = new URL("https://commons.wikimedia.org/w/api.php");
    infoUrl.searchParams.set("action", "query");
    infoUrl.searchParams.set("format", "json");
    infoUrl.searchParams.set("titles", titles);
    infoUrl.searchParams.set("prop", "imageinfo");
    infoUrl.searchParams.set("iiprop", "url|extmetadata|mime|size");
    infoUrl.searchParams.set("iiurlwidth", "800");

    const infoRes = await fetch(infoUrl.toString(), {
      headers: {
        "User-Agent": "RockScout/1.0 (support@rockscout.app)",
        "Accept-Language": "en-US",
      },
    });

    if (!infoRes.ok) {
      return Response.json(
        { error: "Commons image info unavailable" },
        { status: 503, headers: cors },
      );
    }

    const infoData = await infoRes.json() as {
      query?: { pages?: Record<string, { imageinfo?: CommonsImageInfo[] }> };
    };

    const results: CommonsPhotoResult[] = [];
    const pages = infoData.query?.pages || {};

    for (const page of Object.values(pages)) {
      const ii = page.imageinfo?.[0];
      if (!ii) continue;

      // Only accept raster image types
      const mime = ii.mime || "";
      if (!["image/jpeg", "image/png", "image/gif", "image/webp"].includes(mime)) {
        continue;
      }

      // Verify license is PD or CC
      const licenseRaw = ii.extmetadata?.LicenseShortName?.value || "";
      const licenseLower = licenseRaw.toLowerCase();
      if (!FREE_LICENSE_KEYWORDS.some((kw) => licenseLower.includes(kw))) {
        continue;
      }

      const thumbUrl = ii.thumburl || ii.url || "";
      if (!thumbUrl) continue;

      // Clean UTM params that Commons sometimes appends
      const cleanUrl = thumbUrl.split("?")[0];

      const descriptionRaw = ii.extmetadata?.ImageDescription?.value || "";
      const description = stripHtml(descriptionRaw).slice(0, 300);

      const filename = Object.keys(pages).find(
        (k) => pages[k] === page,
      )
        ? ""
        : "";

      results.push({
        url: cleanUrl,
        license: licenseRaw,
        filename: "",
        description,
      });
    }

    return Response.json({ results }, { headers: cors });
  } catch (err) {
    console.error("[commons-photo] error", err);
    return Response.json(
      { error: "Failed to search Commons" },
      { status: 500, headers: cors },
    );
  }
}
