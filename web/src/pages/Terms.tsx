import LegalPage from "@/components/LegalPage";
import { TERMS_SECTIONS, SITE } from "@/content/legal";

const Terms = () => (
  <LegalPage
    title="Terms of Service"
    description={`The rules for using ${SITE.name} — account, acceptable use, subscriptions, user content, trades, disclaimers, and jurisdiction.`}
    intro={`By creating a ${SITE.name} account or using any feature, you agree to these Terms and our Privacy Policy. If you do not agree, do not create an account or use the app.`}
    sections={TERMS_SECTIONS}
  />
);

export default Terms;
