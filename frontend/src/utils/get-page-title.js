import defaultSettings from '@/settings'

const title = defaultSettings.title || ' StackOverflow JAVA Analysis'

export default function getPageTitle(pageTitle) {
  if (pageTitle) {
    return `${pageTitle} - ${title}`
  }
  return `${title}`
}
