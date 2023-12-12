package com.sap.cc.bulletinboard.ads;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

@Component
public class InMemoryAdvertisementStorage {

	private final Map<Long, Advertisement> advertisements = new HashMap<>();

	public Advertisement saveAdvertisement(final Advertisement ad) {
		boolean isAdvertisementIdEmptyOrNonExisting = ad.getId() == null || !advertisements.containsKey(ad.getId());

		if (isAdvertisementIdEmptyOrNonExisting) {
			Long id = advertisements.size() + 1L;
			ad.setId(id);
		}

		advertisements.put(ad.getId(), ad);
		return ad;
	}

	public Optional<Advertisement> retrieveAdvertisementById(Long id) {
		if(id <= 0) {
			throw new IllegalArgumentException("Negative ids are not allowed");
		}
		return Optional.ofNullable(advertisements.get(id));
	}

	public List<Advertisement> retrieveAllAdvertisements() {
		ArrayList<Advertisement> returnValue = new ArrayList<Advertisement>(advertisements.values());
		return returnValue;
	}

	public void deleteAllAdvertisements() {
		advertisements.clear();
	}

	public void deleteAdvertisement(Long id) {
		advertisements.remove(id);
	}

}
