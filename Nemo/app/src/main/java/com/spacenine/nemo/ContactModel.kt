package com.spacenine.nemo

class ContactModel(val id: Int, val name: String, phoneNo: String) {
    val phoneNo: String = validate(phoneNo)

    companion object {
        // validate the phone number, and reformat is necessary
        private fun validate(phone: String): String {

            // creating StringBuilder for both the cases
            val case1 = StringBuilder("+91")
            val case2 = StringBuilder("")

            // check if the string already has a "+"
            return if (phone[0] != '+') {
                for (i in phone.indices) {
                    // remove any spaces or "-"
                    if (phone[i] != '-' && phone[i] != ' ') {
                        case1.append(phone[i])
                    }
                }
                case1.toString()
            } else {
                for (i in phone.indices) {
                    // remove any spaces or "-"
                    if (phone[i] != '-' || phone[i] != ' ') {
                        case2.append(phone[i])
                    }
                }
                case2.toString()
            }
        }
    }
}