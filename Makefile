#------------------------------------------------------------------------------#
# Makefile
# Rémi Attab (remi.attab@gmail.com), 11 Jan 2013
# FreeBSD-style copyright and disclaimer apply
#
# Makefile to setup and run our project.
#------------------------------------------------------------------------------#

INSTALL_DIR ?= ~/Battlecode2013

BOTS := team216 \
	testplayer

install: $(foreach bot,$(BOTS),$(INSTALL_DIR)/teams/$(bot))

$(INSTALL_DIR)/teams/%:
	 ln -s $(CURDIR)/$* $@
