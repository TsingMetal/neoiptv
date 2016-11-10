class Main():
    def test_loop(): 
        firstStep = checkAdcSercurity()
        if first_step == True:
            second_step = checkMacWithDB()
            if second_step == True:
                third_step == writeMac(Mac, SN)
                if third_step == True:
                    fourth_step = send_test_result_to_DB()
                    assert fourth_step == True
                    back_to_first_step()
                else:
                    show_warning("write failed")
                    back_to_first_step()
            else:
                show_warning("check Mac failed")
                back_to_first_step()
        else:
            show_warning("check adv failed")
            back_to_first_step()

    def main():
        test_loop()
