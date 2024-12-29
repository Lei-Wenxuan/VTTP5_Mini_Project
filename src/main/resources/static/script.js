const menuButtons = document.querySelectorAll(".menu-button");
const screenOverlay = document.querySelector(".main-layout .screen-overlay");
const themeButton = document.querySelector(".navbar .theme-button i");
const searchButton = document.querySelector("#search-button");
const searchBackButton = document.querySelector("#search-back-button");

const fullDescriptionText = document.querySelector("#description-text");
const showMoreButton = document.querySelector("#show-more-button");

// Toggle sidebar visibility when menu buttons are clicked
menuButtons.forEach((button) => {
  button.addEventListener("click", () => {
    document.body.classList.toggle("sidebar-hidden");
  });
});

// Toggle sidebar visibility when screen overlay is clicked
screenOverlay.addEventListener("click", () => {
  document.body.classList.toggle("sidebar-hidden");
});

// Initialize dark mode based on localStorage
if (localStorage.getItem("darkMode") === "enabled") {
  document.body.classList.add("dark-mode");
  themeButton.classList.replace("uil-moon", "uil-sun");
} else {
  themeButton.classList.replace("uil-sun", "uil-moon");
}

// Toggle dark mode when theme button is clicked
themeButton.addEventListener("click", () => {
  const isDarkMode = document.body.classList.toggle("dark-mode");
  localStorage.setItem("darkMode", isDarkMode ? "enabled" : "disabled");
  themeButton.classList.toggle("uil-sun", isDarkMode);
  themeButton.classList.toggle("uil-moon", !isDarkMode);
});

// Show sidebar on large screens by default
if (window.innerWidth >= 768) {
  document.body.classList.remove("sidebar-hidden");
}

if (fullDescriptionText) {
  const originalDescriptionText = fullDescriptionText.textContent; // Store the full text
  const descriptionPreviewText = originalDescriptionText.substring(0, 200); // First 200 characters
  fullDescriptionText.textContent = descriptionPreviewText;

  // Toggle the text when the 'Show More' button is clicked
  const toggleDescription = () => {
    if (fullDescriptionText.textContent === descriptionPreviewText) {
      fullDescriptionText.textContent = originalDescriptionText;
      document.querySelector(".show-more-button").textContent = "...less"; // Change button text to 'less'
    } else {
      fullDescriptionText.textContent = descriptionPreviewText;
      document.querySelector(".show-more-button").textContent = "...more"; // Change button text back to 'more'
    }
  };

  showMoreButton.addEventListener("click", toggleDescription);
}

// Toggle search bar on click on mobile
const toggleSearchBar = () => {
  document.body.classList.toggle("show-mobile-search");
};

searchButton.addEventListener("click", toggleSearchBar);
searchBackButton.addEventListener("click", () => searchButton.click());

// Dropdown Menu Functionality
document.querySelectorAll(".link-section .dropdown")?.forEach((dropdown) => {
  dropdown.addEventListener("click", function (e) {
    e.stopPropagation();

    const dropdownMenu = this.querySelector(".dropdown-menu");
    const isOpen = this.classList.contains("open");

    // Helper function to close all child dropdowns
    const closeAllChildDropdowns = (menu) => {
      menu.querySelectorAll(".dropdown").forEach((childDropdown) => {
        childDropdown.classList.remove("open");
        childDropdown
          .querySelector(".link-item.active")
          ?.classList.remove("active");
        const childMenu = childDropdown.querySelector(".dropdown-menu");
        if (childMenu) {
          childMenu.style.height = 0;
        }
      });
    };

    // Function to adjust ancestor heights dynamically
    const adjustAncestorHeights = (menu, deltaHeight) => {
      let parentMenu = menu.closest(".dropdown-menu");
      while (parentMenu) {
        parentMenu.style.height = `${parentMenu.scrollHeight + deltaHeight}px`;
        parentMenu = parentMenu.closest(".dropdown").closest(".dropdown-menu");
      }
    };

    if (isOpen) {
      this.classList.remove("open");
      this.querySelector(".link-item.active").classList.remove("active");
      dropdownMenu.style.height = 0; // Collapse

      // Close all child dropdowns
      closeAllChildDropdowns(dropdownMenu);

      // Adjust ancestor heights
      adjustAncestorHeights(this, -dropdownMenu.scrollHeight);
    } else {
      this.classList.add("open");
      e.target.classList.add("active");
      dropdownMenu.style.height = `${dropdownMenu.scrollHeight}px`; // Expand

      // Adjust ancestor heights
      adjustAncestorHeights(this, dropdownMenu.scrollHeight);
    }
  });
});
