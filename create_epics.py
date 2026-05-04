import requests
import json

# JIRA credentials
JIRA_URL = "https://rohinideviradhamanalan.atlassian.net/"
USERNAME = "rohinidevi.radhamanalan@gmail.com"
API_TOKEN = "ATATT3xFfGF0CNEfOzTCs5bWiOn45zJtfUQGhrUfV2CZQBwskqhe_bthMYjK_v8_Mn0FMxypAa6duspmnKFl9udeHbuN9w5J252IjhyPN5J54XZhDaIypvDxlXjYF99rFCZlWYy3KT2N4ckH5d5mTN0N00ZC54PhGkIqDHh2VkBKrqmc3-rxTLk=FBB2D91D"
PROJECT_KEY = "KAN"

# Auth
auth = (USERNAME, API_TOKEN)

# Get projects
response = requests.get(f"{JIRA_URL}/rest/api/3/project", auth=auth)
if response.status_code != 200:
    print(f"Failed to get projects: {response.status_code} {response.text}")
    exit(1)

projects = response.json()
print("Available projects:")
for proj in projects:
    print(f"  {proj['key']}: {proj['name']}")

# Find project
project_found = None
for proj in projects:
    if proj['key'] == PROJECT_KEY:
        project_found = proj
        break

if not project_found:
    print(f"Project {PROJECT_KEY} not found")
    exit(1)

print(f"Using project: {project_found['name']}")

# Determine epic create metadata
meta_url = f"{JIRA_URL}/rest/api/3/issue/createmeta?projectKeys={PROJECT_KEY}&issuetypeNames=Epic&expand=projects.issuetypes.fields"
response = requests.get(meta_url, auth=auth)
if response.status_code != 200:
    print(f"Failed to get create metadata: {response.status_code} {response.text}")
    exit(1)

meta = response.json()
fields_meta = {}
if meta.get('projects'):
    issue_types_meta = meta['projects'][0].get('issuetypes', [])
    for it in issue_types_meta:
        if it['name'] == 'Epic':
            fields_meta = it.get('fields', {})
            break

print("Epic create metadata fields:")
for field_id, field_info in fields_meta.items():
    name = field_info.get('name')
    required = field_info.get('required', False)
    if 'Epic' in (name or '') or 'Epic Name' in (name or ''):
        print(f"  {field_id}: {name} (required={required})")

# Locate epic name field from create metadata
epic_name_field = None
for field_id, field_info in fields_meta.items():
    if field_info.get('name') == 'Epic Name' or ('epic' in (field_info.get('name') or '').lower() and 'name' in (field_info.get('name') or '').lower()):
        epic_name_field = field_id
        print(f"Using epic name field from create metadata: {epic_name_field}")
        break

if not epic_name_field:
    print("Epic Name field not found in create metadata; proceeding without it")

# Epics data
epics = [
    {
        "summary": "Core Content Delivery Platform",
        "description": "This epic encompasses the fundamental content organization, delivery, and user engagement features that form the backbone of the NBC News platform. It covers the implementation of news categories, content types, homepage navigation, video playback, breaking news alerts, multimedia galleries, user interaction tools, and section-specific features like politics and investigations. The epic ensures users can discover, consume, and engage with diverse news content across web and mobile platforms, supporting the platform's goal of delivering timely, accurate news to millions of users.",
        "epic_name": "Core Content Delivery Platform"
    },
    {
        "summary": "Technical Infrastructure & Performance",
        "description": "This epic addresses the technical requirements for building a scalable, high-performance news platform that can handle millions of users and traffic spikes during major events. It includes implementing the content management system, performance optimization, mobile responsiveness, accessibility compliance, and the underlying technical architecture to support the platform's 99.5% uptime SLA. The epic focuses on creating a robust backend that enables fast content publishing, reliable delivery, and seamless user experiences across all devices.",
        "epic_name": "Technical Infrastructure & Performance"
    },
    {
        "summary": "Content Strategy & Editorial Operations",
        "description": "This epic covers the content creation, publishing, and strategy aspects of the NBC News platform, including the editorial processes for breaking news coverage, investigative reporting, multimedia production, and content scheduling. It encompasses the 24/7 news desk operations, beat reporting structure, video-first content approach, and the systems needed to publish timely, accurate content across multiple formats. The epic ensures the platform can deliver high-quality, diverse content that establishes NBC News as a trusted authority.",
        "epic_name": "Content Strategy & Editorial Operations"
    },
    {
        "summary": "User Experience & Personalization",
        "description": "This epic focuses on creating an intuitive, accessible, and personalized user experience that keeps users engaged with NBC News content. It includes implementing dark mode, content recommendations, personalized dashboards, accessibility features, and user journey optimization from new visitor onboarding to returning user retention. The epic addresses user flows, personalization engines, and experience enhancements that drive repeat visitation and brand loyalty across the platform.",
        "epic_name": "User Experience & Personalization"
    },
    {
        "summary": "Analytics, Tracking & Monetization",
        "description": "This epic implements the analytics infrastructure and monetization systems that enable data-driven decision making and revenue generation for the NBC News platform. It covers user tracking, content performance analytics, advertising integration, programmatic ad serving, and revenue optimization tools. The epic ensures the platform can measure success metrics, optimize ad placements, and support multiple revenue streams while maintaining user privacy and experience.",
        "epic_name": "Analytics, Tracking & Monetization"
    }
]

# Create epics
for epic in epics:
    issue_data = {
        "fields": {
            "project": {
                "key": PROJECT_KEY
            },
            "summary": epic["summary"],
            "description": {
                "type": "doc",
                "version": 1,
                "content": [
                    {
                        "type": "paragraph",
                        "content": [
                            {
                                "type": "text",
                                "text": epic["description"]
                            }
                        ]
                    }
                ]
            },
            "issuetype": {
                "name": "Epic"
            }
        }
    }

    if epic_name_field:
        issue_data["fields"][epic_name_field] = epic["epic_name"]

    response = requests.post(f"{JIRA_URL}/rest/api/3/issue", auth=auth, json=issue_data)
    if response.status_code == 201:
        issue_key = response.json()['key']
        print(f"Created epic: {issue_key} - {epic['summary']}")
    else:
        print(f"Failed to create epic {epic['summary']}: {response.status_code} {response.text}")