import LegalPage from "@/components/LegalPage";
import { COOKIES_SECTIONS, SITE } from "@/content/legal";

const Cookies = () => (
  <LegalPage
    title="Cookies & SDK Disclosure"
    description={`The cookies and SDK identifiers ${SITE.name} uses — AdMob, RevenueCat, Supabase, analytics — and why each one is there.`}
    intro={`${SITE.name}.net and the ${SITE.name} app use a small number of cookies and SDK identifiers. We do not use tracking cookies for ad targeting on the website itself.`}
    sections={COOKIES_SECTIONS}
  />
);

export default Cookies;
