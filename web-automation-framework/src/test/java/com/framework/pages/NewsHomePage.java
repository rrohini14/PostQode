package com.framework.pages;

import com.framework.core.BasePage;
import com.framework.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class NewsHomePage extends BasePage {

    private static final List<By> CATEGORY_MENU_LOCATORS = List.of(
            By.cssSelector("[data-testid='news-categories']"),
            By.cssSelector(".news-categories"),
            By.cssSelector(".category-menu"),
            By.cssSelector("nav[aria-label='News Categories']"),
            By.cssSelector("nav.news-nav")
    );

    private static final By CATEGORY_ITEMS = By.cssSelector(
            "[data-testid='news-categories'] a, " +
            ".news-categories a, " +
            ".category-menu a, " +
            "nav[aria-label='News Categories'] a, " +
            "nav.news-nav a"
    );

    private static final By STORY_CARDS = By.cssSelector(
            "[data-testid='story-card'], article.story-card, .story-card, .news-list article, article[data-category]"
    );

    private static final By FEATURED_SECTION = By.cssSelector(
            "[data-testid='featured-stories'], .featured-stories, section.featured, #featured-stories"
    );

    private static final By FEATURED_STORIES = By.cssSelector(
            "[data-testid='featured-story'], .featured-stories article, section.featured article, #featured-stories article"
    );

    private static final Pattern ARTICLE_COUNT_PATTERN = Pattern.compile(".*\\(\\d+\\).*");

    public boolean isCategoryMenuVisible() {
        return CATEGORY_MENU_LOCATORS.stream()
                .map(driver::findElements)
                .flatMap(List::stream)
                .anyMatch(this::isDisplayed);
    }

    public List<String> getCategoryNames() {
        List<WebElement> categoryElements = driver.findElements(CATEGORY_ITEMS);
        return categoryElements.stream()
                .filter(this::isDisplayed)
                .map(this::getText)
                .filter(text -> !text.isBlank())
                .collect(Collectors.toList());
    }

    public boolean areExpectedCategoriesVisible(List<String> expectedCategories) {
        List<String> actualCategoriesLower = getCategoryNames().stream()
                .map(text -> text.toLowerCase(Locale.ROOT))
                .collect(Collectors.toList());

        for (String expected : expectedCategories) {
            if (!actualCategoriesLower.contains(expected.toLowerCase(Locale.ROOT))) {
                return false;
            }
        }
        return true;
    }

    public void clickCategory(String categoryName) {
        List<WebElement> categoryElements = driver.findElements(CATEGORY_ITEMS);
        for (WebElement element : categoryElements) {
            if (getText(element).equalsIgnoreCase(categoryName)) {
                click(element);
                return;
            }
        }
        throw new IllegalStateException("Category not found in UI: " + categoryName);
    }

    public boolean areOnlySelectedCategoryStoriesDisplayed(String categoryName) {
        WaitUtils.waitUntil(driver, d -> !d.findElements(STORY_CARDS).isEmpty() ? true : null);

        List<WebElement> cards = driver.findElements(STORY_CARDS);
        if (cards.isEmpty()) {
            return false;
        }

        String expected = categoryName.toLowerCase(Locale.ROOT);

        for (WebElement card : cards) {
            String categoryValue = extractCategoryForCard(card);
            if (categoryValue.isBlank()) {
                return false;
            }
            if (!categoryValue.toLowerCase(Locale.ROOT).contains(expected)) {
                return false;
            }
        }
        return true;
    }

    public boolean isCategoryMenuResponsiveForMobile() {
        driver.manage().window().setSize(new Dimension(390, 844));
        boolean result = isCategoryMenuVisible() && !driver.findElements(CATEGORY_ITEMS).isEmpty();
        driver.manage().window().maximize();
        return result;
    }

    public boolean doesEachCategoryShowArticleCount() {
        List<String> categoryTexts = getCategoryNames();
        if (categoryTexts.isEmpty()) {
            return false;
        }
        return categoryTexts.stream().allMatch(text -> ARTICLE_COUNT_PATTERN.matcher(text).matches());
    }

    public boolean isFeaturedSectionVisible() {
        List<WebElement> sections = driver.findElements(FEATURED_SECTION);
        return sections.stream().anyMatch(this::isDisplayed);
    }

    public int getFeaturedStoriesCount() {
        return (int) driver.findElements(FEATURED_STORIES).stream().filter(this::isDisplayed).count();
    }

    public boolean doFeaturedStoriesHaveHeadlineAndImage() {
        List<WebElement> stories = driver.findElements(FEATURED_STORIES);
        if (stories.isEmpty()) {
            return false;
        }

        for (WebElement story : stories) {
            String headline = extractHeadline(story);
            List<WebElement> image = story.findElements(By.cssSelector("img"));
            if (headline.isBlank() || image.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public String clickFirstFeaturedStoryAndGetDestinationUrl() {
        List<WebElement> stories = driver.findElements(FEATURED_STORIES);
        if (stories.isEmpty()) {
            throw new IllegalStateException("No featured stories found");
        }

        WebElement firstStory = stories.get(0);
        List<WebElement> links = firstStory.findElements(By.cssSelector("a[href]"));
        if (links.isEmpty()) {
            throw new IllegalStateException("Featured story does not have clickable link");
        }

        click(links.get(0));
        WaitUtils.waitUntil(driver, d -> !d.getCurrentUrl().isBlank() ? true : null);
        return driver.getCurrentUrl();
    }

    public List<String> getFeaturedStoryTitlesSnapshot() {
        List<WebElement> stories = driver.findElements(FEATURED_STORIES);
        if (stories.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> titles = new ArrayList<>();
        for (WebElement story : stories) {
            String title = extractHeadline(story);
            if (!title.isBlank()) {
                titles.add(title);
            }
        }
        return titles;
    }

    public void refreshHomePage() {
        driver.navigate().refresh();
    }

    // ----- NEW METHODS FOR NEWS-9 (Search functionality) -----
    private static final By SEARCH_BOX = By.cssSelector("[data-testid='search-box'], #search, .search-box, input[type='search']");
    private static final By SEARCH_BUTTON = By.cssSelector("[data-testid='search-button'], .search-button, button[type='submit']");
    private static final By SEARCH_RESULTS = By.cssSelector("[data-testid='search-results'], .search-results, .article-list");
    private static final By SEARCH_RESULT_ITEM = By.cssSelector("[data-testid='search-result'], .search-result, article, .article-item");
    private static final By SEARCH_RESULT_PREVIEW = By.cssSelector(".preview, .excerpt, p");
    private static final By DATE_FILTER = By.cssSelector("[data-testid='date-filter'], .date-filter");
    private static final By DATE_OPTION = By.cssSelector("[data-testid='date-option'], .date-option");

    public boolean isSearchBoxVisible() {
        List<WebElement> elements = driver.findElements(SEARCH_BOX);
        return elements.stream().anyMatch(this::isDisplayed);
    }

    public void enterSearchTerm(String searchTerm) {
        List<WebElement> searchInputs = driver.findElements(SEARCH_BOX);
        if (searchInputs.isEmpty()) {
            throw new IllegalStateException("Search box not found");
        }
        type(searchInputs.get(0), searchTerm);
    }

    public void clickSearchButton() {
        List<WebElement> buttons = driver.findElements(SEARCH_BUTTON);
        if (buttons.isEmpty()) {
            throw new IllegalStateException("Search button not found");
        }
        click(buttons.get(0));
        WaitUtils.waitUntil(driver, d -> !d.findElements(SEARCH_RESULTS).isEmpty() ? true : null);
    }

    public boolean isSearchResultsVisible() {
        List<WebElement> results = driver.findElements(SEARCH_RESULTS);
        return results.stream().anyMatch(this::isDisplayed);
    }

    public List<String> getSearchResultTexts() {
        List<WebElement> results = driver.findElements(SEARCH_RESULT_ITEM);
        return results.stream()
                .filter(this::isDisplayed)
                .map(this::getText)
                .filter(text -> !text.isBlank())
                .toList();
    }

    public List<String> getSearchResultPreviews() {
        List<WebElement> results = driver.findElements(SEARCH_RESULT_ITEM);
        List<String> previews = new ArrayList<>();
        for (WebElement result : results) {
            List<WebElement> previewElements = result.findElements(SEARCH_RESULT_PREVIEW);
            if (!previewElements.isEmpty()) {
                String preview = previewElements.get(0).getText().trim();
                if (!preview.isBlank()) {
                    previews.add(preview);
                }
            }
        }
        return previews;
    }

    public boolean doSearchResultsContainKeyword(String keyword) {
        List<String> results = getSearchResultTexts();
        String keywordLower = keyword.toLowerCase(Locale.ROOT);
        return results.stream().anyMatch(text -> text.toLowerCase(Locale.ROOT).contains(keywordLower));
    }

    public boolean isDateFilterVisible() {
        List<WebElement> filters = driver.findElements(DATE_FILTER);
        return filters.stream().anyMatch(this::isDisplayed);
    }

    public void clickDateFilter() {
        List<WebElement> filters = driver.findElements(DATE_FILTER);
        if (filters.isEmpty()) {
            throw new IllegalStateException("Date filter not found");
        }
        click(filters.get(0));
    }

    public void selectDateRange(String dateRange) {
        List<WebElement> dateOptions = driver.findElements(DATE_OPTION);
        for (WebElement option : dateOptions) {
            if (option.getText().equalsIgnoreCase(dateRange)) {
                click(option);
                return;
            }
        }
        throw new IllegalStateException("Date range option not found: " + dateRange);
    }

    // ----- NEW METHODS FOR NEWS-8 (Multimedia content) -----
    private static final By PHOTO_GALLERY = By.cssSelector("[data-testid='photo-gallery'], .photo-gallery");
    private static final By GALLERY_NEXT_BUTTON = By.cssSelector("[data-testid='gallery-next'], .gallery-next, .next-button");
    private static final By VIDEO_PLAYER = By.cssSelector("video, [data-testid='video-player']");
    private static final By PLAY_BUTTON = By.cssSelector("[data-testid='play-button'], .play-button, video > button");
    private static final By INFOGRAPHIC = By.cssSelector("[data-testid='infographic'], .infographic");

    public boolean isPhotoGalleryVisible() {
        List<WebElement> galleries = driver.findElements(PHOTO_GALLERY);
        return galleries.stream().anyMatch(this::isDisplayed);
    }

    public boolean canNavigateGalleryNext() {
        List<WebElement> nextButtons = driver.findElements(GALLERY_NEXT_BUTTON);
        if (nextButtons.isEmpty()) {
            return false;
        }
        click(nextButtons.get(0));
        return true;
    }

    public boolean isVideoPlayerVisible() {
        List<WebElement> videos = driver.findElements(VIDEO_PLAYER);
        return videos.stream().anyMatch(this::isDisplayed);
    }

    public boolean playVideo() {
        List<WebElement> playButtons = driver.findElements(PLAY_BUTTON);
        if (playButtons.isEmpty()) {
            return false;
        }
        click(playButtons.get(0));
        return true;
    }

    public boolean isInfographicVisible() {
        List<WebElement> infographics = driver.findElements(INFOGRAPHIC);
        return infographics.stream().anyMatch(this::isDisplayed);
    }

    // ----- NEW METHODS FOR NEWS-11 (Related stories) -----
    private static final By RELATED_SECTION = By.cssSelector("[data-testid='related-stories'], .related-stories");
    private static final By RELATED_STORY_ITEMS = By.cssSelector("[data-testid='related-story'], .related-story");
    private static final By RELATED_STORY_LINK = By.cssSelector("a[href]");

    public boolean isRelatedStoriesSectionVisible() {
        List<WebElement> sections = driver.findElements(RELATED_SECTION);
        return sections.stream().anyMatch(this::isDisplayed);
    }

    public List<String> getRelatedStoryCategories() {
        List<WebElement> stories = driver.findElements(RELATED_STORY_ITEMS);
        List<String> categories = new ArrayList<>();
        for (WebElement story : stories) {
            String category = story.getAttribute("data-category");
            if (category != null && !category.isBlank()) {
                categories.add(category.trim());
            }
        }
        return categories;
    }

    public String clickFirstRelatedStoryAndGetUrl() {
        List<WebElement> stories = driver.findElements(RELATED_STORY_ITEMS);
        if (stories.isEmpty()) {
            throw new IllegalStateException("No related stories found");
        }
        List<WebElement> links = stories.get(0).findElements(RELATED_STORY_LINK);
        if (links.isEmpty()) {
            throw new IllegalStateException("Related story does not have a link");
        }
        click(links.get(0));
        WaitUtils.waitUntil(driver, d -> !d.getCurrentUrl().isBlank() ? true : null);
        return driver.getCurrentUrl();
    }

    private String extractHeadline(WebElement story) {
        List<By> headlineLocators = List.of(
                By.cssSelector("[data-testid='headline']"),
                By.cssSelector("h1, h2, h3, h4"),
                By.cssSelector(".headline"),
                By.cssSelector(".title")
        );

        for (By locator : headlineLocators) {
            List<WebElement> elements = story.findElements(locator);
            if (!elements.isEmpty()) {
                String text = elements.get(0).getText().trim();
                if (!text.isBlank()) {
                    return text;
                }
            }
        }
        return "";
    }

    private String extractCategoryForCard(WebElement card) {
        String dataCategory = card.getDomAttribute("data-category");
        if (dataCategory != null && !dataCategory.isBlank()) {
            return dataCategory.trim();
        }

        List<By> candidateLocators = List.of(
                By.cssSelector("[data-testid='story-category']"),
                By.cssSelector(".category"),
                By.cssSelector(".story-category"),
                By.cssSelector("[class*='category']")
        );

        for (By locator : candidateLocators) {
            List<WebElement> elements = card.findElements(locator);
            if (!elements.isEmpty()) {
                String text = elements.get(0).getText().trim();
                if (!text.isBlank()) {
                    return text;
                }
            }
        }

        return "";
    }
}
