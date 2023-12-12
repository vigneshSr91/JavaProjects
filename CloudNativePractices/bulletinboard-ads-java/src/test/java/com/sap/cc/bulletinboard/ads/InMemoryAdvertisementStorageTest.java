package com.sap.cc.bulletinboard.ads;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class InMemoryAdvertisementStorageTest {

	private final InMemoryAdvertisementStorage adStorage = new InMemoryAdvertisementStorage();

	private final Advertisement AD_BIKE = createAdvertisementWithTitleContactPriceCurrency(
			"Bike for Sale", "Bike-sellers", BigDecimal.ONE, "USD");
	private final Advertisement AD_PAN = createAdvertisementWithTitleContactPriceCurrency(
			"Lightly used pan", "Second-hand Pans", BigDecimal.TEN, "EUR");

	@BeforeEach
	public void beforeEach() {
		adStorage.deleteAllAdvertisements();
	}

	@Test
	public void testRetrieveAdvertisementByIdNonExistingAdvertisement() {
		Optional<Advertisement> returnedAdvertisement = adStorage.retrieveAdvertisementById(1L);
		assertThat(returnedAdvertisement.isPresent(), is(false));
	}

	@Test
	public void testSaveAdvertisement() {
		Advertisement returnedAdvertisement = adStorage.saveAdvertisement(AD_BIKE);

		assertThat(returnedAdvertisement.getTitle(), is(AD_BIKE.getTitle()));
		assertThat(returnedAdvertisement.getContact(), is(AD_BIKE.getContact()));
		assertThat(returnedAdvertisement.getId(), is(1L));
		assertThat(returnedAdvertisement.getPrice(), is(BigDecimal.ONE));
		assertThat(returnedAdvertisement.getCurrency(), is("USD"));
	}

	@Test
	public void testSaveTwoAdvertisements() {
		adStorage.saveAdvertisement(AD_BIKE);

		Advertisement returnedAdvertisement = adStorage.saveAdvertisement(AD_PAN);

		assertThat(returnedAdvertisement.getTitle(), is(AD_PAN.getTitle()));
		assertThat(returnedAdvertisement.getContact(), is(AD_PAN.getContact()));
		assertThat(returnedAdvertisement.getId(), is(2L));
		assertThat(returnedAdvertisement.getPrice(), is(BigDecimal.TEN));
		assertThat(returnedAdvertisement.getCurrency(), is("EUR"));
	}

	@Test
	public void testSaveAdvertisementTryToForceId() {
		Advertisement ad = AD_BIKE;
		ad.setId(10L);
		Advertisement returnedAdvertisement = adStorage.saveAdvertisement(ad);

		assertThat(returnedAdvertisement.getId(), is(1L));
	}

	@Test
	public void testSaveAndRetrieveAdvertisementById() {
		adStorage.saveAdvertisement(AD_BIKE);

		Optional<Advertisement> returnedAdvertisement = adStorage.retrieveAdvertisementById(1L);

		assertThat(returnedAdvertisement.isPresent(), is(true));
		assertThat(returnedAdvertisement.get().getId(), is(1L));
		assertThat(returnedAdvertisement.get().getTitle(), is(AD_BIKE.getTitle()));
		assertThat(returnedAdvertisement.get().getContact(), is(AD_BIKE.getContact()));
		assertThat(returnedAdvertisement.get().getPrice(), is(BigDecimal.ONE));
		assertThat(returnedAdvertisement.get().getCurrency(), is("USD"));

	}

	@Test
	public void testUpdateTitleOfExistingAdvertisement() {
		Advertisement returnedAdvertisement = adStorage.saveAdvertisement(AD_BIKE);
		assertThat(returnedAdvertisement.getId(), is(1L));

		final String newTitle = "Bruce Schneier";
		returnedAdvertisement.setTitle(newTitle);

		adStorage.saveAdvertisement(returnedAdvertisement);

		assertThat(returnedAdvertisement.getTitle(), is(newTitle));
		assertThat(returnedAdvertisement.getId(), is(1L));

	}

	@Test
	public void testUpdatePriceOfExistingAdvertisement() {
		Advertisement returnedAdvertisement = adStorage.saveAdvertisement(AD_BIKE);
		assertThat(returnedAdvertisement.getId(), is(1L));

		final BigDecimal newPrice = BigDecimal.valueOf(2L);
		returnedAdvertisement.setPrice(newPrice);

		adStorage.saveAdvertisement(returnedAdvertisement);

		assertThat(returnedAdvertisement.getPrice(), is(newPrice));
		assertThat(returnedAdvertisement.getId(), is(1L));

	}

	@Test
	public void testGetAllEmpty() {
		List<Advertisement> returnedAdvertisements = adStorage.retrieveAllAdvertisements();
		assertThat(returnedAdvertisements.size(), is(0));
	}

	@Test
	public void testGetAllFirstOneThenTwoEntries() {

		adStorage.saveAdvertisement(AD_BIKE);

		List<Advertisement> returnedAdvertisements = adStorage.retrieveAllAdvertisements();
		assertThat(returnedAdvertisements.size(), is(1));
		Advertisement ad = returnedAdvertisements.get(0);
		assertThat(ad.getTitle(), is(AD_BIKE.getTitle()));
		assertThat(ad.getContact(), is(AD_BIKE.getContact()));
		assertThat(ad.getId(), is(1L));
		assertThat(ad.getPrice(), is(BigDecimal.ONE));
		assertThat(ad.getCurrency(), is("USD"));

		adStorage.saveAdvertisement(AD_PAN);

		returnedAdvertisements = adStorage.retrieveAllAdvertisements();
		assertThat(returnedAdvertisements.size(), is(2));

	}

	@Test
	public void testRetrieveAdvertisementByIdThrowsExceptionForNegativeValue() {

		assertThatThrownBy(() ->
		{
		adStorage.retrieveAdvertisementById(-1L);
		}).isInstanceOf(IllegalArgumentException.class);

	}

	@Test
	public void testDeleteSingle() {

		adStorage.saveAdvertisement(AD_BIKE);
		adStorage.saveAdvertisement(AD_PAN);

		List<Advertisement> returnedAdvertisements = adStorage.retrieveAllAdvertisements();
		assertThat(returnedAdvertisements.size(), is(2));

		adStorage.deleteAdvertisement(1L);

		returnedAdvertisements = adStorage.retrieveAllAdvertisements();

		assertThat(returnedAdvertisements.size(), is(1));
		Advertisement ad = returnedAdvertisements.get(0);
		assertThat(ad.getTitle(), is(AD_PAN.getTitle()));
		assertThat(ad.getContact(), is(AD_PAN.getContact()));
		assertThat(ad.getId(), is(2L));

	}

	@Test
	public void testDeleteAll() {

		adStorage.saveAdvertisement(AD_BIKE);
		adStorage.saveAdvertisement(AD_PAN);

		List<Advertisement> returnedAdvertisements = adStorage.retrieveAllAdvertisements();
		assertThat(returnedAdvertisements.size(), is(2));

		adStorage.deleteAllAdvertisements();

		returnedAdvertisements = adStorage.retrieveAllAdvertisements();
		assertThat(returnedAdvertisements.size(), is(0));
	}

	private Advertisement createAdvertisementWithTitleContactPriceCurrency(
			String title, String contact, BigDecimal price, String currency) {
		Advertisement ad = new Advertisement();
		ad.setTitle(title);
		ad.setContact(contact);
		ad.setPrice(price);
		ad.setCurrency(currency);
		return ad;
	}

}
