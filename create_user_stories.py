import requests

JIRA_URL = "https://rohinideviradhamanalan.atlassian.net/"
USERNAME = "rohinidevi.radhamanalan@gmail.com"
API_TOKEN = "ATATT3xFfGF0CNEfOzTCs5bWiOn45zJtfUQGhrUfV2CZQBwskqhe_bthMYjK_v8_Mn0FMxypAa6duspmnKFl9udeHbuN9w5J252IjhyPN5J54XZhDaIypvDxlXjYF99rFCZlWYy3KT2N4ckH5d5mTN0N00ZC54PhGkIqDHh2VkBKrqmc3-rxTLk=FBB2D91D"
PROJECT_KEY = "KAN"
ISSUE_TYPE_NAME = "Task"

auth = (USERNAME, API_TOKEN)

stories_by_epic = {
    "KAN-1": [
        {
            "summary": "User Story: Browse news by category",
            "description": "As a reader, I want to browse news by category so I can find relevant stories quickly."
        },
        {
            "summary": "User Story: Highlight featured homepage stories",
            "description": "As a reader, I want a featured story section on the homepage so I can see top news at a glance."
        },
        {
            "summary": "User Story: Support multimedia content in articles",
            "description": "As a reader, I want multimedia content like photo galleries and videos within articles so I can engage with rich content."
        },
        {
            "summary": "User Story: Provide search across news archives",
            "description": "As a reader, I want a search field so I can locate articles across the site."
        },
        {
            "summary": "User Story: Organize clear editorial categories",
            "description": "As an editor, I want news categories for breaking, national, and world so I can organize content clearly."
        },
        {
            "summary": "User Story: Offer related story suggestions",
            "description": "As a reader, I want related story suggestions on article pages so I can continue exploring news topics."
        },
        {
            "summary": "User Story: Ensure responsive homepage layout",
            "description": "As a visitor, I want a responsive homepage layout so I can use the site on mobile devices."
        },
        {
            "summary": "User Story: Deliver breaking news alerts",
            "description": "As a reader, I want breaking news alerts so I can be informed of urgent updates."
        },
        {
            "summary": "User Story: Enable article commenting and sharing",
            "description": "As a reader, I want article comment and share actions so I can discuss and distribute news."
        },
        {
            "summary": "User Story: Design section-specific layouts",
            "description": "As a reporter, I want dedicated section layouts for politics and investigations so my content is presented with focus."
        }
    ],
    "KAN-2": [
        {
            "summary": "User Story: Meet fast page load targets",
            "description": "As a platform owner, I want the site to load within 2.5 seconds so users have a fast experience."
        },
        {
            "summary": "User Story: Deliver mobile-responsive pages",
            "description": "As a developer, I want the site to be mobile responsive so it works on phones and tablets."
        },
        {
            "summary": "User Story: Ensure high availability",
            "description": "As an operations lead, I want 99.5% uptime so the news site is available during major events."
        },
        {
            "summary": "User Story: Implement editorial CMS workflows",
            "description": "As an editor, I want a CMS workflow for publishing so content is reviewed before release."
        },
        {
            "summary": "User Story: Secure platform traffic with TLS",
            "description": "As a security officer, I want SSL/TLS encryption so user data is protected."
        },
        {
            "summary": "User Story: Add CDN support for global delivery",
            "description": "As a system admin, I want CDN support so content is delivered quickly worldwide."
        },
        {
            "summary": "User Story: Schedule and publish editorial content",
            "description": "As a product manager, I want editorial scheduling workflows so content is published on time."
        },
        {
            "summary": "User Story: Provide accessible interfaces",
            "description": "As a user, I want accessible interfaces meeting WCAG 2.1 AA so the site is usable by everyone."
        },
        {
            "summary": "User Story: Validate platform scalability",
            "description": "As an engineer, I want scalability testing so the platform can handle traffic spikes."
        },
        {
            "summary": "User Story: Monitor performance metrics",
            "description": "As a performance analyst, I want monitoring dashboards so we can track delivery metrics."
        }
    ],
    "KAN-3": [
        {
            "summary": "User Story: Publish breaking news quickly",
            "description": "As a news editor, I want a 24/7 breaking news coverage workflow so urgent stories are published quickly."
        },
        {
            "summary": "User Story: Standardize beat reporting",
            "description": "As a reporter, I want beat reporting templates for politics and business so I can standardize coverage."
        },
        {
            "summary": "User Story: Support investigative reporting",
            "description": "As an investigative journalist, I want support for multi-part investigations so I can publish deep reporting."
        },
        {
            "summary": "User Story: Publish video-first stories",
            "description": "As a content manager, I want video-first publishing for major stories so audiences get visual coverage."
        },
        {
            "summary": "User Story: Enable archive search",
            "description": "As an editor, I want an archive search facility so I can retrieve past content easily."
        },
        {
            "summary": "User Story: Prepare seasonal coverage",
            "description": "As a publisher, I want seasonal coverage planning for elections and holidays so we can prepare content in advance."
        },
        {
            "summary": "User Story: Tag content for discoverability",
            "description": "As a newsroom lead, I want metadata tagging for content so stories are discoverable."
        },
        {
            "summary": "User Story: Publish live event coverage",
            "description": "As a producer, I want live event coverage capabilities so we can broadcast real-time updates."
        },
        {
            "summary": "User Story: Maintain evergreen reference content",
            "description": "As a manager, I want evergreen content guidelines so we can maintain useful reference material."
        },
        {
            "summary": "User Story: Enforce editorial approval checkpoints",
            "description": "As a journalist, I want editorial approval checkpoints so content quality and accuracy are ensured."
        }
    ],
    "KAN-4": [
        {
            "summary": "User Story: Personalize recommendations",
            "description": "As a returning reader, I want personalized story recommendations so I see content matched to my interests."
        },
        {
            "summary": "User Story: Support dark mode",
            "description": "As a reader, I want dark mode so I can browse comfortably at night."
        },
        {
            "summary": "User Story: Simplify navigation for new visitors",
            "description": "As a new visitor, I want a clear navigation menu so I can find news sections quickly."
        },
        {
            "summary": "User Story: Save articles for later reading",
            "description": "As a user, I want ability to save articles so I can read them later."
        },
        {
            "summary": "User Story: Set category preferences",
            "description": "As a reader, I want category filter preferences so I can focus on topics I care about."
        },
        {
            "summary": "User Story: Ensure accessible page layouts",
            "description": "As a product designer, I want accessible page layouts so all users can read comfortably."
        },
        {
            "summary": "User Story: Manage notification preferences",
            "description": "As a frequent visitor, I want notification preferences so I only receive relevant alerts."
        },
        {
            "summary": "User Story: Show related content suggestions",
            "description": "As a researcher, I want related content suggestions so I can explore similar topics."
        },
        {
            "summary": "User Story: Return fast search results",
            "description": "As a mobile user, I want quick search results so I can find articles faster."
        },
        {
            "summary": "User Story: Set up A/B testing for UX",
            "description": "As a UX analyst, I want A/B testing setup so we can optimize onboarding and engagement."
        }
    ],
    "KAN-5": [
        {
            "summary": "User Story: Track page analytics",
            "description": "As a business lead, I want page analytics tracking so we can measure traffic and engagement."
        },
        {
            "summary": "User Story: Track video analytics",
            "description": "As a marketing manager, I want video analytics so we can understand playback performance."
        },
        {
            "summary": "User Story: Place advertising support",
            "description": "As a revenue owner, I want advertising placeholders for display and video ads."
        },
        {
            "summary": "User Story: Build content performance dashboards",
            "description": "As a data analyst, I want content performance dashboards so we can identify high-performing stories."
        },
        {
            "summary": "User Story: Use privacy-compliant tracking",
            "description": "As a product owner, I want anonymized user tracking so we can improve content without compromising privacy."
        },
        {
            "summary": "User Story: Add programmatic ad integration",
            "description": "As an engineer, I want programmatic ad integration so ad revenue can scale with traffic."
        },
        {
            "summary": "User Story: Report CPM and conversions",
            "description": "As a business manager, I want CPM and conversion reporting so we can evaluate monetization health."
        },
        {
            "summary": "User Story: Enable A/B testing experiments",
            "description": "As an experiment owner, I want A/B testing capabilities so we can validate UX changes."
        },
        {
            "summary": "User Story: Log critical events and errors",
            "description": "As an operations lead, I want event logging for alerts and errors so we can troubleshoot issues quickly."
        },
        {
            "summary": "User Story: Measure newsletter signups",
            "description": "As a strategist, I want newsletter signup metrics so we can grow audience retention."
        }
    ]
}

for epic_key, stories in stories_by_epic.items():
    for story in stories:
        issue_data = {
            "fields": {
                "project": {
                    "key": PROJECT_KEY
                },
                "summary": story["summary"],
                "description": {
                    "type": "doc",
                    "version": 1,
                    "content": [
                        {
                            "type": "paragraph",
                            "content": [
                                {
                                    "type": "text",
                                    "text": story["description"]
                                }
                            ]
                        }
                    ]
                },
                "issuetype": {
                    "name": ISSUE_TYPE_NAME
                },
                "parent": {
                    "key": epic_key
                }
            }
        }
        response = requests.post(f"{JIRA_URL}/rest/api/3/issue", auth=auth, json=issue_data)
        if response.status_code == 201:
            issue_key = response.json()["key"]
            print(f"Created task: {issue_key} -> {story['summary']}")
        else:
            print(f"Failed to create story under {epic_key}: {response.status_code} {response.text}")
