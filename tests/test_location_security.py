import unittest
from pathlib import Path
ROOT = Path(__file__).resolve().parents[1]

class DotWeatherLocationTests(unittest.TestCase):
    def test_ip_provider_is_https_only(self):
        source = (ROOT / "app/src/main/java/com/example/dotweather/data/WeatherApiClient.kt").read_text(encoding="utf-8")
        self.assertIn('httpGet("https://ipwho.is/")', source)
        self.assertNotIn('http://ip-api.com', source)

    def test_ip_derived_location_is_not_claimed_as_gps(self):
        manifest = (ROOT / "app/src/main/AndroidManifest.xml").read_text(encoding="utf-8")
        self.assertNotIn('ACCESS_COARSE_LOCATION', manifest)
        self.assertNotIn('ACCESS_FINE_LOCATION', manifest)
        main = (ROOT / "app/src/main/java/com/example/dotweather/ui/main/MainScreen.kt").read_text(encoding="utf-8")
        self.assertNotIn('isGps = true', main)
        self.assertIn('isGps = false', main)

if __name__ == "__main__":
    unittest.main()
