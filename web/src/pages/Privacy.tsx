import LegalPage from "@/components/LegalPage";
import { PRIVACY_SECTIONS, SITE } from "@/content/legal";

const Privacy = () => (
  <LegalPage
    title="Privacy Policy"
    description={`How ${SITE.name} collects, uses, and protects your data — camera, location, notifications, auth, subscriptions, ads, and content.`}
    intro={`${SITE.name} collects only what it needs to run its features. This policy explains what we collect, why, and the choices you have. We do not sell your personal information.`}
    sections={PRIVACY_SECTIONS}
  />
);

export default Privacy;
