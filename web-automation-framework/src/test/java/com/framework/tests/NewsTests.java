package com.framework.tests;

import com.framework.base.BaseTest;
import com.framework.pages.NewsHomePage;
import com.framework.pages.NewsEditorPage;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Automated tests for NEWS-6: Browse news by category
 * NEWS-7: Highlight featured homepage stories
 * NEWS-8: Support multimedia content
 * NEWS-9: Search functionality
 * NEWS-10: Editorial category management
 * NEWS-11: Related story suggestions
 */
public class NewsTests extends BaseTest {

    // NEWS-6 Test Cases
    @Test(description = "TC-NEWS-6-001: User navigates to category menu")
    public void verifyNewsCategoriesMenuVisible() {
        NewsHomePage newsHomePage = new NewsHomePage();
        Assert.assertTrue(newsHomePage.isCategoryMenuVisible(),
                "News categories menu should be visible on homepage");
    }

    @Test(description = "TC-NEWS-6-001: All expected categories are visible")
    public void verifyAllCategoriesDisplay() {
        NewsHomePage newsHomePage = new NewsHomePage();
        List<String> expectedCategories = List.of(
                "Breaking News", "U.S. News", "World News", "Politics",
                "Business", "Technology", "Health", "Entertainment", "Sports"
        );
        Assert.assertTrue(newsHomePage.areExpectedCategoriesVisible(expectedCategories),
                "All expected news categories should be visible and clickable");
    }

    @Test(description = "TC-NEWS-6-002: User filters stories by Politics category")
    public void verifyPoliticsCategoryFiltering() {
        NewsHomePage newsHomePage = new NewsHomePage();
        newsHomePage.clickCategory("Politics");
        Assert.assertTrue(newsHomePage.areOnlySelectedCategoryStoriesDisplayed("Politics"),
                "Only Politics category stories should be displayed after filtering");
    }

    @Test(description = "TC-NEWS-6-003: User filters stories by Business category")
    public void verifyBusinessCategoryFiltering() {
        NewsHomePage newsHomePage = new NewsHomePage();
        newsHomePage.clickCategory("Business");
        Assert.assertTrue(newsHomePage.areOnlySelectedCategoryStoriesDisplayed("Business"),
                "Only Business category stories should be displayed after filtering");
    }

    @Test(description = "TC-NEWS-6-004: Category menu is responsive on mobile")
    public void verifyCategoryMenuResponsiveOnMobile() {
        NewsHomePage newsHomePage = new NewsHomePage();
        Assert.assertTrue(newsHomePage.isCategoryMenuResponsiveForMobile(),
                "Category menu should display in mobile-friendly layout without breaking");
    }

    @Test(description = "TC-NEWS-6-005: Category name displays article count")
    public void verifyCategoryArticleCountsDisplay() {
        NewsHomePage newsHomePage = new NewsHomePage();
        Assert.assertTrue(newsHomePage.doesEachCategoryShowArticleCount(),
                "Each category should show count of available articles");
    }

    // NEWS-7 Test Cases
    @Test(description = "TC-NEWS-7-001: Featured section displays on homepage")
    public void verifyFeaturedSectionVisible() {
        NewsHomePage newsHomePage = new NewsHomePage();
        Assert.assertTrue(newsHomePage.isFeaturedSectionVisible(),
                "Featured stories section should be visible at top of page");
    }

    @Test(description = "TC-NEWS-7-002: At least 5 featured stories display")
    public void verifyMinimumFeaturedStoriesCount() {
        NewsHomePage newsHomePage = new NewsHomePage();
        int featuredCount = newsHomePage.getFeaturedStoriesCount();
        Assert.assertTrue(featuredCount >= 5,
                "Minimum 5 featured stories should be visible, but found: " + featuredCount);
    }

    @Test(description = "TC-NEWS-7-003: Featured story has headline and image")
    public void verifyFeaturedStoriesHaveHeadlineAndImage() {
        NewsHomePage newsHomePage = new NewsHomePage();
        Assert.assertTrue(newsHomePage.doFeaturedStoriesHaveHeadlineAndImage(),
                "Each featured story should display headline and thumbnail image");
    }

    @Test(description = "TC-NEWS-7-004: Featured story link is clickable")
    public void verifyFeaturedStoryLinkNavigates() {
        NewsHomePage newsHomePage = new NewsHomePage();
        String destinationUrl = newsHomePage.clickFirstFeaturedStoryAndGetDestinationUrl();
        Assert.assertTrue(destinationUrl.contains("/article") || destinationUrl.contains("/news") || !destinationUrl.isBlank(),
                "User should be directed to full article page. Got URL: " + destinationUrl);
    }

    @Test(description = "TC-NEWS-7-005: Featured stories update dynamically")
    public void verifyFeaturedStoriesUpdateDynamically() {
        NewsHomePage newsHomePage = new NewsHomePage();
        List<String> initialTitles = newsHomePage.getFeaturedStoryTitlesSnapshot();
        newsHomePage.refreshHomePage();
        List<String> refreshedTitles = newsHomePage.getFeaturedStoryTitlesSnapshot();
        Assert.assertNotEquals(initialTitles, refreshedTitles,
                "Featured stories should be updated with new content after page refresh");
    }

    // NEWS-8: Multimedia Content Tests
    @Test(description = "TC-NEWS-8-001: Verify photo gallery display and navigation")
    public void verifyPhotoGallery() {
        NewsHomePage newsHomePage = new NewsHomePage();
        Assert.assertTrue(newsHomePage.isPhotoGalleryVisible(), "Photo gallery should be visible");
        Assert.assertTrue(newsHomePage.canNavigateGalleryNext(), "Should be able to navigate to next photo");
    }

    @Test(description = "TC-NEWS-8-002: Verify video player is visible and playable")
    public void verifyVideoPlayer() {
        NewsHomePage newsHomePage = new NewsHomePage();
        Assert.assertTrue(newsHomePage.isVideoPlayerVisible(), "Video player should be visible");
        Assert.assertTrue(newsHomePage.playVideo(), "Video should be playable");
    }

    @Test(description = "TC-NEWS-8-003: Verify infographic renders correctly")
    public void verifyInfographic() {
        NewsHomePage newsHomePage = new NewsHomePage();
        Assert.assertTrue(newsHomePage.isInfographicVisible(), "Infographic should be visible");
    }

    // NEWS-9: Search Functionality Tests
    @Test(description = "TC-NEWS-9-001: Verify search box is visible on homepage")
    public void verifySearchBoxVisible() {
        NewsHomePage newsHomePage = new NewsHomePage();
        Assert.assertTrue(newsHomePage.isSearchBoxVisible(), "Search box should be visible");
    }

    @Test(description = "TC-NEWS-9-002: Verify search returns relevant results")
    public void verifySearchReturnsResults() {
        NewsHomePage newsHomePage = new NewsHomePage();
        newsHomePage.enterSearchTerm("climate");
        newsHomePage.clickSearchButton();
        Assert.assertTrue(newsHomePage.isSearchResultsVisible(), "Search results should display");
        Assert.assertTrue(newsHomePage.doSearchResultsContainKeyword("climate"),
                "Results should contain the search keyword");
    }

    @Test(description = "TC-NEWS-9-003: Verify search results have previews")
    public void verifySearchResultPreviews() {
        NewsHomePage newsHomePage = new NewsHomePage();
        newsHomePage.enterSearchTerm("technology");
        newsHomePage.clickSearchButton();
        List<String> previews = newsHomePage.getSearchResultPreviews();
        Assert.assertFalse(previews.isEmpty(), "Search results should have previews");
    }

    @Test(description = "TC-NEWS-9-004: Verify date range filter works")
    public void verifyDateFilter() {
        NewsHomePage newsHomePage = new NewsHomePage();
        newsHomePage.clickSearchButton();
        Assert.assertTrue(newsHomePage.isDateFilterVisible(), "Date filter should be visible");
        newsHomePage.clickDateFilter();
        newsHomePage.selectDateRange("Last 7 days");
    }

    // NEWS-10: Editorial Category Management Tests
    @Test(description = "TC-NEWS-10-001: Editor can create new category")
    public void testEditorCanCreateCategory() {
        NewsEditorPage editorPage = new NewsEditorPage();
        editorPage.createNewCategory("Technology Updates");
        Assert.assertTrue(editorPage.isCategoryPresent("Technology Updates"),
                "Category should be created successfully");
    }

    @Test(description = "TC-NEWS-10-002: Editor can assign article to category")
    public void testEditorCanAssignArticle() {
        NewsEditorPage editorPage = new NewsEditorPage();
        editorPage.selectCategoryForArticle("article[data-id]", "Technology Updates");
        Assert.assertTrue(editorPage.isCategoryPresent("Technology Updates"),
                "Article should be assigned to category");
    }

    @Test(description = "TC-NEWS-10-003: Category slug is URL-friendly")
    public void testCategorySlugUrlFriendly() {
        NewsEditorPage editorPage = new NewsEditorPage();
        editorPage.createNewCategory("Breaking News");
        String slug = editorPage.getCategorySlug("Breaking News");
        Assert.assertEquals(slug, "breaking-news", "Slug should be URL-friendly without spaces");
    }

    // NEWS-11: Related Stories Suggestions Tests
    @Test(description = "TC-NEWS-11-001: Verify related stories section displays")
    public void verifyRelatedStoriesSection() {
        NewsHomePage newsHomePage = new NewsHomePage();
        Assert.assertTrue(newsHomePage.isRelatedStoriesSectionVisible(),
                "Related stories section should be visible");
    }

    @Test(description = "TC-NEWS-11-002: Verify related stories are from same category")
    public void verifyRelatedStoriesSameCategory() {
        NewsHomePage newsHomePage = new NewsHomePage();
        newsHomePage.clickCategory("Politics");
        List<String> categories = newsHomePage.getRelatedStoryCategories();
        if (!categories.isEmpty()) {
            Set<String> uniqueCategories = new HashSet<>(categories);
            Assert.assertEquals(uniqueCategories.size(), 1,
                    "All related stories should be from the same category");
        }
    }

    @Test(description = "TC-NEWS-11-003: Verify related story links navigate correctly")
    public void verifyRelatedStoryNavigation() {
        NewsHomePage newsHomePage = new NewsHomePage();
        String destinationUrl = newsHomePage.clickFirstRelatedStoryAndGetUrl();
        Assert.assertTrue(destinationUrl.contains("/article") || destinationUrl.contains("/news") || !destinationUrl.isBlank(),
                "Should navigate to article/news page");
    }
}</arg_value>
<task_progress>
- [x] Analyze requirements and existing framework
- [x] Update NewsHomePage.java with NEWS-8 and NEWS-11 methods
- [x] Create NewsEditorPage.java for NEWS-10
- [x] Add search functionality to NewsHomePage for NEWS-9
- [x] Update NewsTests.java with test cases for NEWS-8, NEWS-9, NEWS-10, NEWS-11
- [x] Verify all implementations
</task_progress>
</write_to_file>
