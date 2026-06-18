package com.foodiego.foodiego.controller;

import com.foodiego.foodiego.entity.Address;
import com.foodiego.foodiego.repository.AddressRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/profile/addresses")
public class AddressController {

        private static final String REDIRECT_ADDRESSES = "redirect:/profile/addresses";

        private final AddressRepository addressRepository;

        public AddressController(
                        AddressRepository addressRepository) {

                this.addressRepository = addressRepository;
        }

        @GetMapping
        public String addresses(
                        HttpSession session,
                        Model model) {

                String email = (String) session.getAttribute(
                                "loggedInUser");

                model.addAttribute(
                                "addresses",

                                addressRepository
                                                .findByUserEmail(
                                                                email));

                return "addresses";
        }

        @PostMapping("/add")
        public String addAddress(
                        Address address,
                        HttpSession session) {

                address.setUserEmail(

                                (String) session.getAttribute(
                                                "loggedInUser"));

                addressRepository.save(
                                address);

                return REDIRECT_ADDRESSES;
        }

        @PostMapping("/delete/{id}")
        public String deleteAddress(
                        @PathVariable Long id) {

                addressRepository.deleteById(
                                id);

                return REDIRECT_ADDRESSES;
        }

        @GetMapping("/edit/{id}")
        public String editAddress(
                        @PathVariable Long id,
                        Model model) {

                model.addAttribute(
                                "address",

                                addressRepository
                                                .findById(id)
                                                .orElseThrow());

                return "edit-address";
        }

        @PostMapping("/update")
        public String updateAddress(

                        @RequestParam Long id,

                        @RequestParam String fullName,

                        @RequestParam String phone,

                        @RequestParam String addressLine,

                        @RequestParam String city,

                        @RequestParam String state,

                        @RequestParam String pincode) {

                // validate inputs
                if (!fullName.matches("[A-Za-z ]{3,50}")) {
                        return "redirect:/profile/addresses?error=name";
                }

                if (!phone.matches("\\d{10}")) {
                        return "redirect:/profile/addresses?error=phone";
                }

                if (addressLine == null || addressLine.trim().length() < 10) {
                        return "redirect:/profile/addresses/edit/" + id + "?error=address";
                }

                if (!pincode.matches("\\d{6}")) {
                        return "redirect:/profile/addresses?error=pincode";
                }

                if (!city.matches("[A-Za-z ]+")) {
                        return "redirect:/profile/addresses?error=city";
                }

                if (!state.matches("[A-Za-z ]+")) {
                        return "redirect:/profile/addresses?error=state";
                }

                Address address = addressRepository
                                .findById(id)
                                .orElseThrow();

                address.setFullName(fullName);

                address.setPhone(phone);

                address.setAddressLine(addressLine);

                address.setCity(city);

                address.setState(state);

                address.setPincode(pincode);

                addressRepository.save(address);

                return REDIRECT_ADDRESSES;
        }
}