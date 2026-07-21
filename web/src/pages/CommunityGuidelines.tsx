import LegalPage from "@/components/LegalPage";
import { COMMUNITY_SECTIONS, SITE } from "@/content/legal";

const CommunityGuidelines = () => (
  <LegalPage
    title="Community Guidelines"
    description={`The rules for posting, commenting, messaging, and trading in the ${SITE.name} community.`}
    intro={`${SITE.name} is a community of rockhounds, collectors, and learners. These guidelines apply to every post, comment, message, trade listing, profile detail, and image you share.`}
    sections={COMMUNITY_SECTIONS}
  />
);

export default CommunityGuidelines;
